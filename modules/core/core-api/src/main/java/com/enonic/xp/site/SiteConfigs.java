package com.enonic.xp.site;

import java.util.Collection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class SiteConfigs
    extends AbstractImmutableEntityList<SiteConfig>
{
    private static final SiteConfigs EMPTY = new SiteConfigs( ImmutableList.of() );

    private SiteConfigs( final ImmutableList<SiteConfig> list )
    {
        super( list );
    }

    public SiteConfig get( final ApplicationKey applicationKey )
    {
        return list.stream().filter( sc -> applicationKey.equals( sc.getApplicationKey() ) ).findAny().orElse( null );
    }

    @Deprecated
    public SiteConfig get( final String applicationKey )
    {
        return get( ApplicationKey.from( applicationKey ) );
    }

    @Deprecated
    public ImmutableSet<ApplicationKey> getApplicationKeys()
    {
        return list.stream().map( SiteConfig::getApplicationKey ).collect( ImmutableSet.toImmutableSet() );
    }

    public static SiteConfigs empty()
    {
        return EMPTY;
    }

    public static SiteConfigs from( final SiteConfig... siteConfigs )
    {
        return fromInternal( ImmutableList.copyOf( siteConfigs ) );
    }

    public static SiteConfigs from( final Iterable<? extends SiteConfig> siteConfigs )
    {
        return fromInternal( ImmutableList.copyOf( siteConfigs ) );
    }

    public static SiteConfigs from( final Collection<? extends SiteConfig> siteConfigs )
    {
        return fromInternal( ImmutableList.copyOf( siteConfigs ) );
    }

    private static SiteConfigs fromInternal( final ImmutableList<SiteConfig> siteConfigs )
    {
        if ( siteConfigs.isEmpty() )
        {
            return EMPTY;
        }
        else
        {
            return new SiteConfigs( siteConfigs );
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<SiteConfig> builder = ImmutableList.builder();

        public Builder add( final SiteConfig siteConfig )
        {
            builder.add( siteConfig );
            return this;
        }

        public SiteConfigs build()
        {
            return fromInternal( builder.build() );
        }
    }
}
