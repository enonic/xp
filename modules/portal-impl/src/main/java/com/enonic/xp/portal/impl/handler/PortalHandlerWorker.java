package com.enonic.xp.portal.impl.handler;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.PortalException;

public abstract class PortalHandlerWorker
{
    protected PortalRequest request;

    protected PortalResponse.Builder response;

    public abstract void execute()
        throws Exception;

    protected final PortalException notFound( final String message, final Object... args )
    {
        return PortalException.notFound( String.format( message, args ) );
    }
}
