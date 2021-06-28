package com.inject.plugin.converter;

import com.intellij.psi.PsiField;

/**
 * Created time : 2021/6/27 15:39.
 *
 * @author 10585
 */
public class AddAnnotationField {
    public final String id;
    public final PsiField field;

    public AddAnnotationField(String id, PsiField field) {
        this.id = id;
        this.field = field;
    }
}