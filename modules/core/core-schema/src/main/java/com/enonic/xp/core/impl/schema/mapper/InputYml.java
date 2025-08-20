package com.enonic.xp.core.impl.schema.mapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItemType;
import com.enonic.xp.schema.I18NText;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InputYml
    extends FormItem
{
    public String type;

    public String name;

    public I18NText label;

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
