package com.enonic.wem.portal.url;

import com.google.common.collect.Multimap;

import static com.google.common.base.Strings.emptyToNull;

public final class GeneralUrlBuilder
    extends PortalUrlBuilder<GeneralUrlBuilder>
{
    private String path;

    public GeneralUrlBuilder path( final String value )
    {
        this.path = emptyToNull( value );
        return this;
    }

    @Override
    protected void buildUrl( final StringBuilder url, final Multimap<String, String> params )
    {
        super.buildUrl( url, params );

        if ( this.path != null )
        {
            appendPart( url, this.path );
        }
    }
}
