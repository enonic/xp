package com.enonic.wem.api.content.schema.content.form;


public abstract class HierarchicalFormItem
    extends FormItem
{

    HierarchicalFormItem( final String name )
    {
        super( name );
    }


    @Override
    public String toString()
    {
        final FormItemPath formItemPath = getPath();
        if ( formItemPath != null )
        {
            return formItemPath.toString();
        }
        else
        {
            return getName() + "?";
        }
    }
}
