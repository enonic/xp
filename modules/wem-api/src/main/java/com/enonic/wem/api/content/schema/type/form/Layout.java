package com.enonic.wem.api.content.schema.type.form;


public abstract class Layout
    extends FormItem
{
    Layout()
    {
    }

    public abstract FormItem getFormItem( final String name );
}
