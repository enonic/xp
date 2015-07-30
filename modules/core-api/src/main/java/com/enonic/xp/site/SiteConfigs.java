package com.enonic.xp.site;

import java.util.Collection;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.support.AbstractImmutableEntityList;

@Beta
public class SiteConfigs
    extends AbstractImmutableEntityList<SiteConfig>
{
    private final ImmutableMap<ApplicationKey, SiteConfig> applicationsByName;

    private SiteConfigs( final ImmutableList<SiteConfig> list )
    {
        super( list );
        this.applicationsByName = Maps.uniqueIndex( list, new ToNameFunction() );
    }

    public SiteConfig get( final ApplicationKey applicationKey )
    {
        return this.applicationsByName.get( applicationKey );
    }

    public SiteConfig get( final String applicationKey )
    {
        return get( ApplicationKey.from( applicationKey ) );
    }

    public static SiteConfigs empty()
    {
        final ImmutableList<SiteConfig> list = ImmutableList.of();
        return new SiteConfigs( list );
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

    private final static class ToNameFunction
        implements Function<SiteConfig, ApplicationKey>
    {
        @Override
        public ApplicationKey apply( final SiteConfig value )
        {
            return value.getApplicationKey();
        }
    }
}
