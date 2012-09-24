package com.enonic.wem.core.jcr.old.accounts;

public enum Gender
{
    MALE,
    FEMALE;

    public static Gender fromName( String name )
    {
        if ( name == null )
        {
            return null;
        }
        try
        {
            return Gender.valueOf( name.toUpperCase() );
        }
        catch ( IllegalArgumentException e )
        {
            return null;
        }
    }
}
