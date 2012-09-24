package com.enonic.wem.api.content.type.formitem;


public abstract class Layout
    extends FormItem
{
    Layout()
    {
    }

    public abstract FormItem getFormItem( final String name );
}
