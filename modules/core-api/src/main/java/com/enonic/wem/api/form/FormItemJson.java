package com.enonic.wem.api.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@SuppressWarnings("UnusedDeclaration")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes(
    {@JsonSubTypes.Type(value = InputJson.class, name = "Input"), @JsonSubTypes.Type(value = FormItemSetJson.class, name = "FormItemSet"),
        @JsonSubTypes.Type(value = FieldSetJson.class, name = "FieldSet"),
        @JsonSubTypes.Type(value = InlineJson.class, name = "Inline")})
public abstract class FormItemJson<T extends FormItem>
{
    public abstract String getName();

    @JsonIgnore
    public abstract T getFormItem();

}
