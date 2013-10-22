package com.enonic.wem.admin.json.form;

import com.enonic.wem.api.form.Layout;

@SuppressWarnings("UnusedDeclaration")
public abstract class LayoutJson
    extends FormItemJson
{
    private Layout layout;

    public LayoutJson( final Layout layout )
    {
        super( layout );
        this.layout = layout;
    }

    public String getFormItemType()
    {
        return Layout.class.getSimpleName();
    }

    public String getLayoutType()
    {
        return layout.getClass().getSimpleName();
    }
}
