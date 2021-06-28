package com.inject.plugin.converter;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilBase;

import org.jetbrains.annotations.NotNull;
import org.jf.util.SparseIntArray;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created time : 2021/6/27 14:18.
 *
 * @author 10585
 */
public class ConvertAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        Editor editor = event.getData(PlatformDataKeys.EDITOR);

        actionPerformedImpl(project, editor);
    }

    //$1 type $2 fieldName $3  $4 R.id.*
    Pattern mPattern = Pattern.compile("([A-Za-z0-9]+\\s+)*([a-zA-Z_0-9]+)\\s+=\\s+(view\\.)*findViewById\\((R\\.id\\.[a-z_]+)\\);");


    private void actionPerformedImpl(Project project, Editor editor) {
        PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);
        PsiClass psiClass = getTargetClass(editor, file);
        PsiField[] fields = psiClass.getAllFields();

        PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);

        Document document = editor.getDocument();
        String text = document.getText();

        String[] split = text.split("\n");

        boolean find;
        List<ViewField> needCreate = new ArrayList<>();
        List<AddAnnotationField> psiFields = new ArrayList<>();

        List<DeleteLine> deleteLine = new ArrayList<>();


        for (String str : split) {
            find = false;
            Matcher matcher = mPattern.matcher(str);
            if (matcher.find()) {
                String name = matcher.group(2);
                String id = matcher.group(4);
                for (PsiField field : fields) {
                    if (field.getName().equals(name)) {
                        find = true;
                        psiFields.add(new AddAnnotationField(id, field));
                        break;
                    }
                }
                if (!find) {
                    needCreate.add(new ViewField(name, matcher.group(1), id));
                }
                int index = text.indexOf(str);
                deleteLine.add(new DeleteLine(index, index + str.length()));
            }
        }

        new ConvertWriter(project,
                psiClass, factory, document, needCreate, psiFields, deleteLine
        ).execute();
    }


    protected PsiClass getTargetClass(Editor editor, PsiFile file) {
        int offset = editor.getCaretModel().getOffset();
        PsiElement element = file.findElementAt(offset);
        if (element == null) {
            return null;
        } else {
            PsiClass target = PsiTreeUtil.getParentOfType(element, PsiClass.class);
            return target instanceof SyntheticElement ? null : target;
        }
    }
}
