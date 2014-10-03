package com.enonic.wem.api.content.site;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.data.DataId;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.module.ModuleKey;

public final class Site
    extends Content
{
    public Site( final Builder builder )
    {
        super( builder );
    }

    public String getDescription()
    {
        return this.getContentData().getProperty( "description" ).getString();
    }

    public ModuleConfig getModuleConfig( final ModuleKey moduleKey )
    {
        return this.getModuleConfigs().get( moduleKey );
    }

    public ModuleConfigs getModuleConfigs()
    {
        return new ModuleConfigsDataSerializer().fromData( this.getContentData() ).build();
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

    public static Builder newSite()
    {
        return new Builder();
    }

    public static Builder newSite( final Site source )
    {
        return new Builder( source );
    }

    public static class Builder
        extends Content.Builder<Builder, Site>
    {
        private static final ModuleConfigsDataSerializer MODULE_CONFIGS_DATA_SERIALIZER = new ModuleConfigsDataSerializer();

        public Builder( final Site source )
        {
            super( source );
        }

        public Builder()
        {
            super();
        }

        public Builder description( final String description )
        {

            if ( contentData == null )
            {
                contentData = new ContentData();
            }
            contentData.setProperty( DataId.from( "description", 0 ), Value.newString( description ) );
            return this;
        }

        public Builder addModuleConfig( ModuleConfig moduleConfig )
        {
            if ( contentData == null )
            {
                contentData = new ContentData();
            }
            MODULE_CONFIGS_DATA_SERIALIZER.addToData( moduleConfig, contentData );

            return this;
        }

        public Builder moduleConfigs( ModuleConfigs moduleConfigs )
        {
            if ( contentData == null )
            {
                contentData = new ContentData();
            }
            MODULE_CONFIGS_DATA_SERIALIZER.toData( moduleConfigs ).forEach( contentData::add );
            return this;
        }

        public Site build()
        {
            return new Site( this );
        }

    }
}
