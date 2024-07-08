package com.enonic.xp.site.api;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public class SiteApiMountDescriptors
    extends AbstractImmutableEntityList<SiteApiMountDescriptor>
{
    private SiteApiMountDescriptors( final ImmutableList<SiteApiMountDescriptor> list )
    {
        super( list );
    }

    public static SiteApiMountDescriptors empty()
    {
        return new SiteApiMountDescriptors( ImmutableList.of() );
    }

    public static SiteApiMountDescriptors from( final SiteApiMountDescriptor... siteApiMountDescriptors )
    {
        return new SiteApiMountDescriptors( ImmutableList.copyOf( siteApiMountDescriptors ) );
    }

    public static SiteApiMountDescriptors from( final Iterable<? extends SiteApiMountDescriptor> siteApiDescriptors )
    {
        return new SiteApiMountDescriptors( ImmutableList.copyOf( siteApiDescriptors ) );
    }
}
