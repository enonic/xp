package com.enonic.wem.admin.json.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.enonic.wem.api.form.FormItem;

@SuppressWarnings("UnusedDeclaration")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes(
    {@JsonSubTypes.Type(value = InputJson.class, name = "Input"), @JsonSubTypes.Type(value = FormItemSetJson.class, name = "FormItemSet"),
        @JsonSubTypes.Type(value = LayoutJson.class, name = "Layout"), @JsonSubTypes.Type(value = MixinReferenceJson.class, name = "MixinReference")})
public abstract class FormItemJson<T extends FormItem>
{
    public abstract String getName();

    @JsonIgnore
    public abstract T  getFormItem();

}
