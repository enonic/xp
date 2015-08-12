package com.enonic.xp.admin.impl.json.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.annotations.Beta;

import com.enonic.xp.form.Layout;

@Beta
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
