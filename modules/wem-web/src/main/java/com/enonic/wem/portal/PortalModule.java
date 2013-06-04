package com.enonic.wem.portal;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import com.enonic.wem.portal.service.AttachmentService;
import com.enonic.wem.portal.service.AttachmentServiceImpl;
import com.enonic.wem.portal.service.ContentService;
import com.enonic.wem.portal.service.ContentServiceImpl;
import com.enonic.wem.portal.service.ImageService;
import com.enonic.wem.portal.service.ImageServiceImpl;
import com.enonic.wem.portal.service.SpaceService;
import com.enonic.wem.portal.service.SpaceServiceImpl;

public final class PortalModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        install( new PortalServletModule() );
        bind( ContentService.class ).to( ContentServiceImpl.class ).in( Scopes.SINGLETON );
        bind( AttachmentService.class ).to( AttachmentServiceImpl.class ).in( Scopes.SINGLETON );
        bind( SpaceService.class ).to( SpaceServiceImpl.class ).in( Scopes.SINGLETON );
        bind( ImageService.class ).to( ImageServiceImpl.class ).in( Scopes.SINGLETON );
    }
}
