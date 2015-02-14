package com.enonic.xp.core.form.inputtype;

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
