package com.enonic.xp.site;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.content.Content;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;

@PublicApi
public final class Site
    extends Content
{
    private Site( final Builder builder )
    {
        super( builder );
    }

    @Deprecated
    public String getDescription()
    {
        return this.getData().getString( "description" );
    }

    @Deprecated
    public SiteConfigs getSiteConfigs()
    {
        return new SiteConfigsDataSerializer().fromProperties( this.getData().getRoot() ).build();
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        return super.equals( o );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final Site source )
    {
        return new Builder( source );
    }

    public static class Builder
        extends Content.Builder<Builder>
    {
        private static final SiteConfigsDataSerializer SITE_CONFIGS_DATA_SERIALIZER = new SiteConfigsDataSerializer();

        public Builder( final Site source )
        {
            super( source );
        }

        public Builder()
        {
            super();
            type = ContentTypeName.site();
        }

        public Builder description( final String description )
        {

            if ( data == null )
            {
                data = new PropertyTree();
            }
            data.setString( "description", description );
            return this;
        }

        public Builder addSiteConfig( final SiteConfig siteConfig )
        {
            if ( data == null )
            {
                data = new PropertyTree();
            }
            SITE_CONFIGS_DATA_SERIALIZER.toProperties( siteConfig, data.getRoot() );

            return this;
        }

        public Builder siteConfigs( final SiteConfigs siteConfigs )
        {
            if ( data == null )
            {
                data = new PropertyTree();
            }
            SITE_CONFIGS_DATA_SERIALIZER.toProperties( siteConfigs, data.getRoot() );
            return this;
        }

        @Override
        public Site build()
        {
            return new Site( this );
        }

    }
}
