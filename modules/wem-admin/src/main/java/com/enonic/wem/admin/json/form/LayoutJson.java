package com.enonic.wem.admin.json.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.enonic.wem.api.form.Layout;

@SuppressWarnings("UnusedDeclaration")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes({@JsonSubTypes.Type(value = FieldSetJson.class, name = "FieldSet")})
public abstract class LayoutJson<T extends Layout>
    extends FormItemJson<T>
{
    private T layout;

    public LayoutJson( final T layout )
    {
        this.layout = layout;
    }

    @JsonIgnore
    @Override
    public T getFormItem()
    {
        return layout;
    }

    @Override
    public String getName()
    {
        return layout.getName();
    }
}
