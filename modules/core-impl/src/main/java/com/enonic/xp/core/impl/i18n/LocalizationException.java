/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.xp.core.impl.i18n;

public final class LocalizationException
    extends RuntimeException
{
    public LocalizationException( final String message, final Throwable t )
    {
        super( message, t );
    }

    public LocalizationException( final String message )
    {
        super( message );
    }
}
