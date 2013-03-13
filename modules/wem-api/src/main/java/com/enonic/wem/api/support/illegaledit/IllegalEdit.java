package com.enonic.wem.api.support.illegaledit;


public class IllegalEdit
{
    public static void check( String property, Object from, Object to, Class objectClass )
    {
        if ( from == null && to == null )
        {
            return;
        }

        if ( from == null )
        {
            throw new IllegalEditException( property, from, to, objectClass );
        }
        else if ( !from.equals( to ) )
        {
            throw new IllegalEditException( property, from, to, objectClass );
        }
    }
}
