package com.enonic.xp.lib.portal.url;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.AttachmentUrlParams;

public final class AttachmentUrlHandler
    extends AbstractUrlHandler
{
    @Override
    protected String buildUrl( final Multimap<String, String> map )
    {
        final AttachmentUrlParams params = new AttachmentUrlParams().portalRequest( this.request ).setAsMap( map );
        return this.urlService.attachmentUrl( params );
    }
}
