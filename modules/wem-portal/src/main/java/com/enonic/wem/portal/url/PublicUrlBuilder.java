package com.enonic.wem.portal.url;

import java.util.Map;

import static com.google.common.base.Strings.emptyToNull;

// TODO: Asset is probably a better name.
public final class PublicUrlBuilder
    extends PortalUrlBuilder<PublicUrlBuilder>
{
    private String resourcePath;

    private String module;

    public PublicUrlBuilder module( final String module )
    {
        this.module = emptyToNull( module );
        return this;
    }

    public PublicUrlBuilder resourcePath( final String resourcePath )
    {
        this.resourcePath = emptyToNull( resourcePath );
        return this;
    }

    @Override
    protected void buildUrl( final StringBuilder url, final Map<String, String> params )
    {
        super.buildUrl( url, params );
        appendPart( url, "_" );
        appendPart( url, "public" );
        appendPart( url, this.module );
        appendPart( url, this.resourcePath );
    }
}
