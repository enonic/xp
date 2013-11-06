package com.enonic.wem.admin.json.form;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.enonic.wem.api.form.Layout;

@SuppressWarnings("UnusedDeclaration")
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
