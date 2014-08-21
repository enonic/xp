package com.enonic.wem.portal.internal.base;

import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

final class NotFoundResource
    extends ServerResource
{
    @Override
    protected void doInit()
        throws ResourceException
    {
        setExisting( false );
    }
}
