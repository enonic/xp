package com.enonic.xp.form;

import com.google.common.annotations.Beta;

@Beta
public abstract class Layout
    extends FormItem
{

    Layout()
    {
    }

    @Override
    public FormItemType getType()
    {
        return FormItemType.LAYOUT;
    }

    public abstract FormItem getFormItem( final String name );
}
