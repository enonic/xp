package com.enonic.wem.api.support.illegalchange;


public class IllegalChange
{
    public static void check( String property, Object from, Object to, Class objectClass )
    {
        if ( from == null && to == null )
        {
            return;
        }

        if ( from == null )
        {
            throw new IllegalChangeException( property, from, to, objectClass );
        }
        else if ( !from.equals( to ) )
        {
            throw new IllegalChangeException( property, from, to, objectClass );
        }
    }
}
