package com.enonic.xp.core.impl.schema.mapper;

import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItemType;
import com.enonic.xp.form.Occurrences;
import com.enonic.xp.schema.LocalizedText;

public class InputYml
    extends FormItem
{
    public String type;

    public String name;

    public LocalizedText label;

    public LocalizedText helpText;

    public Occurrences occurrences;

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public FormItemType getType()
    {
        return FormItemType.INPUT;
    }

    @Override
    public FormItem copy()
    {
        return InputRegistry.toInput( this );
    }
}
