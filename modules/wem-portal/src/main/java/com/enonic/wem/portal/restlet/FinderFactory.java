package com.enonic.wem.portal.restlet;

import org.restlet.resource.Finder;
import org.restlet.resource.ServerResource;

public interface FinderFactory
{
    public Finder finder( Class<? extends ServerResource> type );
}
