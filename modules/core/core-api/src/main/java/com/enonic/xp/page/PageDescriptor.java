package com.enonic.xp.page;


import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

import com.enonic.xp.form.Form;
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.resource.ResourceKey;

@Beta
public final class PageDescriptor
{
    private final DescriptorKey key;

    private final String displayName;

    private final String displayNameI18nKey;

    private final Form config;

    private final RegionDescriptors regions;

    private PageDescriptor( final Builder builder )
    {
        Preconditions.checkNotNull( builder.key, "key cannot be null" );
        Preconditions.checkNotNull( builder.config, "config cannot be null" );
        Preconditions.checkNotNull( builder.regions, "regions cannot be null" );
        this.key = builder.key;
        this.displayName = builder.displayName;
        this.regions = builder.regions;
        this.config = builder.config;
        this.displayNameI18nKey = builder.displayNameI18nKey;
    }

    public DescriptorKey getKey()
    {
        return key;
    }

    public String getName()
    {
        return this.key.getName();
    }

    public ResourceKey getResourceKey()
    {
        return ResourceKey.from( key.getApplicationKey(), "site/pages/" + key.getName() );
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getDisplayNameI18nKey()
    {
        return displayNameI18nKey;
    }

    public Form getConfig()
    {
        return config;
    }

    public RegionDescriptors getRegions()
    {
        return regions;
    }

    public static ResourceKey toResourceKey( final DescriptorKey key )
    {
        return ResourceKey.from( key.getApplicationKey(), "site/pages/" + key.getName() + "/" + key.getName() + ".xml" );
    }

    public static PageDescriptor.Builder create()
    {
        return new Builder();
    }

    public static PageDescriptor.Builder copyOf( final PageDescriptor pageDescriptor )
    {
        return new Builder( pageDescriptor );
    }

    public static class Builder
    {
        private DescriptorKey key;

        private String displayName;

        private String displayNameI18nKey;

        private Form config;

        private RegionDescriptors regions;

        private Builder( final PageDescriptor pageDescriptor )
        {
            this.key = pageDescriptor.getKey();
            this.displayName = pageDescriptor.getDisplayName();
            this.displayNameI18nKey = pageDescriptor.getDisplayNameI18nKey();
            this.config = pageDescriptor.getConfig();
            this.regions = pageDescriptor.getRegions();
        }

        private Builder()
        {
        }

        public Builder key( final DescriptorKey value )
        {
            this.key = value;
            return this;
        }

        public Builder displayName( final String value )
        {
            this.displayName = value;
            return this;
        }

        public Builder config( final Form value )
        {
            this.config = value;
            return this;
        }

        public Builder regions( final RegionDescriptors value )
        {
            this.regions = value;
            return this;
        }

        public Builder displayNameI18nKey( final String displayNameI18nKey )
        {
            this.displayNameI18nKey = displayNameI18nKey;
            return this;
        }

        public PageDescriptor build()
        {
            return new PageDescriptor( this );
        }
    }
}
