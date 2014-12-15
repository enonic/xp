package com.enonic.xp.portal.url;

import com.google.common.collect.Multimap;

import static com.google.common.base.Strings.emptyToNull;

public final class ComponentUrlBuilder
    extends PortalUrlBuilder<ComponentUrlBuilder>
{
    private String component;

    public ComponentUrlBuilder component( final String value )
    {
        this.component = emptyToNull( value );
        return this;
    }

    @Override
    protected void buildUrl( final StringBuilder url, final Multimap<String, String> params )
    {
        super.buildUrl( url, params );
        appendPart( url, "_" );
        appendPart( url, "component" );
        appendPart( url, this.component );
    }
}
