package com.inject.plugin.converter;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;

import java.util.List;
import java.util.Objects;

/**
 * Created time : 2021/6/27 15:36.
 *
 * @author 10585
 */
public class ConvertWriter extends WriteCommandAction.Simple<Object> {
    final PsiClass psiClass;
    final PsiElementFactory factory;
    final Document document;
    //需要创建成员变量的
    final List<ViewField> needCreate;
    //需要添加注解的变量
    final List<AddAnnotationField> psiFields;
    //需要删除的代码
    final List<DeleteLine> deleteLine;

    protected ConvertWriter(Project project, PsiClass psiClass, PsiElementFactory factory,
                            Document document, List<ViewField> needCreate,
                            List<AddAnnotationField> psiFields, List<DeleteLine> deleteLine) {
        super(project, "Convert FindViewById to Inject");
        this.psiClass = psiClass;
        this.factory = factory;
        this.document = document;
        this.needCreate = needCreate;
        this.psiFields = psiFields;
        this.deleteLine = deleteLine;
    }

    @Override
    protected void run() throws Throwable {
        //先删除掉findviewbyid
        for (int i = deleteLine.size()-1; i >= 0; i--) {
            DeleteLine line = this.deleteLine.get(i);
            document.deleteString(line.start, line.end);
        }
//        for (DeleteLine line : deleteLine) {
//            document.deleteString(line.start, line.end);
//        }
        PsiDocumentManager manager = PsiDocumentManager.getInstance(psiClass.getProject());
        manager.commitDocument(document);


        for (AddAnnotationField field : psiFields) {
            PsiModifierList modifierList = field.field.getModifierList();
            if (modifierList != null) {
                if (!modifierList.hasModifierProperty(PsiModifier.STATIC)) {

                    String builder = "@BindView(" + '"' + field.id + '"' + ") ";
                    PsiAnnotation annotationFromText = factory.createAnnotationFromText(builder, psiClass);
//                    modifierList.delete();
                    if (modifierList.hasModifierProperty(PsiModifier.PRIVATE)) {
                        modifierList.setModifierProperty(PsiModifier.PRIVATE,false);
                    }
                    modifierList.setModifierProperty(PsiModifier.PACKAGE_LOCAL,true);
                    field.field.addBefore(annotationFromText, field.field);
                }
            }

        }

        for (ViewField field : needCreate) {
            String builder = "@BindView(" + '"' +
                    field.id + '"' + ") " +
                    field.type +
                    " " +
                    field.name +
                    ";";

            psiClass.add(factory.createFieldFromText(builder, psiClass));
        }

        addImport("com.inject.annotation.BindView");
    }


    private void addImport(String clazz) {
        PsiFile file = psiClass.getContainingFile();
        if (file instanceof PsiJavaFile) {
            PsiJavaFile psiJavaFile = (PsiJavaFile) file;
            PsiImportList importList = psiJavaFile.getImportList();

            if (null != importList) {
                PsiImportStatement[] statements = importList.getImportStatements();
                for (PsiImportStatement statement : statements) {
                    if (Objects.equals(statement.getQualifiedName(), clazz)) {
                        return;
                    }
                }
            }
        }

        PsiClass importClass = JavaPsiFacade.getInstance(psiClass.getProject()).findClass(clazz,
                GlobalSearchScope.projectScope(psiClass.getProject()));

        if (importClass == null) {
            importClass = JavaPsiFacade.getInstance(psiClass.getProject()).findClass(clazz,
                    ProjectScope.getLibrariesScope(psiClass.getProject()));
        }
        if (importClass != null) {
            psiClass.addBefore(factory.createImportStatement(importClass), psiClass.getFirstChild());
        }
    }
}