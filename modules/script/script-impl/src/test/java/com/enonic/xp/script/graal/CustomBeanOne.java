package com.enonic.xp.script.graal;

public class CustomBeanOne
{
    private String value;

    public void setValue( final String value )
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

    public void execute()
    {
        System.out.println( "Value: " + value );
    }
}
