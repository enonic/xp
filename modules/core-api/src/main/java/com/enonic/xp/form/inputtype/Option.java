package com.enonic.xp.form.inputtype;

import com.google.common.annotations.Beta;

@Beta
public class Option
{
    private String label;

    private String value;

    Option( final String label, final String value )
    {
        this.label = label;
        this.value = value;
    }

    public String getLabel()
    {
        return label;
    }

    public String getValue()
    {
        return value;
    }
}
