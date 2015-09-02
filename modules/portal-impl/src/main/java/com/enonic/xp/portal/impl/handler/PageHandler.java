package com.enonic.xp.portal.impl.handler;

import org.osgi.service.component.annotations.Component;

import com.google.common.base.Strings;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.PortalHandler2;

@Component(immediate = true, service = PortalHandler2.class)
public final class PageHandler
    extends BaseHandler
{
    public PageHandler()
    {
        super( 100 );
    }

    @Override
    public boolean canHandle( final PortalRequest req )
    {
        return Strings.isNullOrEmpty( req.getEndpointPath() );
    }

    @Override
    protected PortalResponse doHandle( final PortalRequest req )
        throws Exception
    {
        final ContentPath path = req.getContentPath();
        return PortalResponse.create().status( 200 ).body( "Page " + req.getBranch() + ", " + path ).build();
    }
}
