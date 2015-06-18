package com.enonic.xp.lib.portal.url;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.AttachmentUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;

final class AttachmentUrlHandler
    extends AbstractUrlHandler
{
    public AttachmentUrlHandler( final PortalUrlService urlService )
    {
        super( urlService );
    }

    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {
        final AttachmentUrlParams params = new AttachmentUrlParams().portalRequest( getPortalRequest() ).setAsMap( map );
        return this.urlService.attachmentUrl( params );
    }

}
