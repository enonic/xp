package com.enonic.wem.core.jcr.old.accounts;

public enum JcrAccountType
{
    USER,
    GROUP,
    ROLE;

    public static JcrAccountType fromName( String name )
    {
        if ( name == null )
        {
            return null;
        }
        try
        {
            return JcrAccountType.valueOf( name.toUpperCase() );
        }
        catch ( IllegalArgumentException e )
        {
            return null;
        }
    }
}
