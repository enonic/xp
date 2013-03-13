package com.enonic.wem.api.support.illegaledit;


public class IllegalEditException
    extends RuntimeException
{
    IllegalEditException( final String property, final Object from, final Object to, final Class objectClass )
    {
        super( buildMesasge( property, from, to, objectClass ) );
    }

    private static String buildMesasge( final String property, final Object from, final Object to, final Class objectClass )
    {
        return objectClass.getSimpleName() + "." + property + " cannot be changed: [" + from + "] -> [" + to + "]";
    }
}
