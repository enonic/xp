package com.enonic.xp.portal.impl.url;

import java.util.Objects;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.url.BaseUrlStrategy;
import com.enonic.xp.portal.url.UrlTypeConstants;

final class SlashApiRewritableBaseUrlStrategy
    implements BaseUrlStrategy
{
    private final ApplicationKey applicationKey;

    private final PortalRequest portalRequest;

    private final String apiKey;

    private final String urlType;

    private SlashApiRewritableBaseUrlStrategy( final ApplicationKey applicationKey, final String apiKey, final PortalRequest portalRequest,
                                               final String urlType )
    {
        this.applicationKey = Objects.requireNonNull( applicationKey );
        this.apiKey = Objects.requireNonNull( apiKey );
        this.portalRequest = Objects.requireNonNull( portalRequest );
        this.urlType = Objects.requireNonNullElse( urlType, UrlTypeConstants.SERVER_RELATIVE );
    }

    static SlashApiRewritableBaseUrlStrategy forApi( final ApplicationKey applicationKey, final String apiKey,
                                                     final PortalRequest portalRequest, final String urlType )
    {
        return new SlashApiRewritableBaseUrlStrategy( applicationKey, apiKey, portalRequest, urlType );
    }

    @Override
    public String generateBaseUrl()
    {
        return UrlBuilderHelper.rewriteUri( portalRequest.getRawRequest(), urlType, "/api/" + getDescriptorKey() );
    }

    public DescriptorKey getDescriptorKey()
    {
        return DescriptorKey.from( applicationKey, apiKey );
    }
}
