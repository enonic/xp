package com.enonic.xp.site;

import java.util.Collection;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public class SiteConfigs
    extends AbstractImmutableEntityList<SiteConfig>
{
    private final ImmutableMap<ApplicationKey, SiteConfig> applicationsByName;

    private SiteConfigs( final ImmutableList<SiteConfig> list )
    {
        super( list );
        this.applicationsByName =
            list.stream().collect( ImmutableMap.toImmutableMap( SiteConfig::getApplicationKey, Function.identity() ) );
    }

    public SiteConfig get( final ApplicationKey applicationKey )
    {
        return this.applicationsByName.get( applicationKey );
    }

    public SiteConfig get( final String applicationKey )
    {
        return get( ApplicationKey.from( applicationKey ) );
    }

    public ImmutableSet<ApplicationKey> getApplicationKeys()
    {
        return applicationsByName.keySet();
    }

    public static SiteConfigs empty()
    {
        return new SiteConfigs( ImmutableList.of() );
    }

    public static SiteConfigs from( final SiteConfig... siteConfigs )
    {
        return new SiteConfigs( ImmutableList.copyOf( siteConfigs ) );
    }

    public static SiteConfigs from( final Iterable<? extends SiteConfig> siteConfigs )
    {
        return new SiteConfigs( ImmutableList.copyOf( siteConfigs ) );
    }

    public static SiteConfigs from( final Collection<? extends SiteConfig> siteConfigs )
    {
        return new SiteConfigs( ImmutableList.copyOf( siteConfigs ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ImmutableList.Builder<SiteConfig> builder = ImmutableList.builder();

        public Builder add( SiteConfig siteConfig )
        {
            builder.add( siteConfig );
            return this;
        }

        public SiteConfigs build()
        {
            return new SiteConfigs( builder.build() );
        }
    }
}
