package com.enonic.xp.portal.url;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.PortalContext;

public interface PortalUrlService
{
    public String assetUrl( PortalContext context, Multimap<String, String> params );

    public String serviceUrl( PortalContext context, Multimap<String, String> params );

    public String imageUrl( PortalContext context, Multimap<String, String> params );

    public String componentUrl( PortalContext context, Multimap<String, String> params );

    public String pageUrl( PortalContext context, Multimap<String, String> params );

    public String attachmentUrl( PortalContext context, Multimap<String, String> params );
}
