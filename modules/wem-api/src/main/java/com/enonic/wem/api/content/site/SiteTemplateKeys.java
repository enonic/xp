package com.enonic.wem.api.content.site;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.support.AbstractImmutableEntityList;

public final class SiteTemplateKeys
    extends AbstractImmutableEntityList<SiteTemplateKey>
{
    private SiteTemplateKeys( final ImmutableList<SiteTemplateKey> list )
    {
        super( list );
    }

    public static SiteTemplateKeys from( final SiteTemplateKey... siteTemplateKeys )
    {
        return new SiteTemplateKeys( ImmutableList.copyOf( siteTemplateKeys ) );
    }

    public static SiteTemplateKeys from( final Iterable<SiteTemplateKey> siteTemplateKeys )
    {
        return new SiteTemplateKeys( ImmutableList.copyOf( siteTemplateKeys ) );
    }

    public static SiteTemplateKeys from( final Collection<SiteTemplateKey> siteTemplateKeys )
    {
        return new SiteTemplateKeys( ImmutableList.copyOf( siteTemplateKeys ) );
    }

    public static SiteTemplateKeys empty()
    {
        return new SiteTemplateKeys( ImmutableList.<SiteTemplateKey>of() );
    }
}
