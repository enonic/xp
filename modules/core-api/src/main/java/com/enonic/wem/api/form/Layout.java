package com.enonic.wem.api.form;


public abstract class Layout
    extends FormItem
{
    Layout( final String name )
    {
        super( name );
    }


    @Override
    public FormItemType getType()
    {
        return FormItemType.LAYOUT;
    }

    @Override
    FormItemPath resolvePath()
    {
        return resolveParentPath();
    }

    public abstract FormItem getFormItem( final String name );
}
