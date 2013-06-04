package com.enonic.wem.portal;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import com.enonic.wem.portal.attachment.AttachmentService;
import com.enonic.wem.portal.attachment.AttachmentServiceImpl;
import com.enonic.wem.portal.content.ContentService;
import com.enonic.wem.portal.content.ContentServiceImpl;
import com.enonic.wem.portal.dispatch.SpaceService;
import com.enonic.wem.portal.dispatch.SpaceServiceImpl;
import com.enonic.wem.portal.image.ImageService;
import com.enonic.wem.portal.image.ImageServiceImpl;

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
