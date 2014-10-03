package com.enonic.wem.api.content.site;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.support.Changes;

import static com.enonic.wem.api.support.PossibleChange.newPossibleChange;

public final class Site
    extends Content
{
    private Site( final SiteEditBuilder builder )
    {
        super( builder );
    }

    public Site( final Builder builder )
    {
        super( builder );
    }

    public String getDescription()
    {
        return this.getContentData().getProperty( "description" ).getString();
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

    public static SiteEditBuilder editSite( final Site toBeEdited )
    {
        return new SiteEditBuilder( toBeEdited );
    }

    public static class SiteEditBuilder
        extends Content.EditBuilder
    {
        private static final ModuleConfigsDataSerializer MODULE_CONFIGS_DATA_SERIALIZER = new ModuleConfigsDataSerializer();

        private final Site original;

        private final Changes.Builder changes = new Changes.Builder();

        public SiteEditBuilder( final Site original )
        {
            super( original );
            this.original = original;
        }

        public SiteEditBuilder moduleConfigs( ModuleConfigs moduleConfigs )
        {
            changes.recordChange(
                newPossibleChange( "moduleConfigs" ).from( original.getModuleConfigs().getList() ).to( moduleConfigs.getList() ).build() );

            if ( contentData == null )
            {
                contentData = new ContentData();
            }
            for ( final Property property : MODULE_CONFIGS_DATA_SERIALIZER.toData( moduleConfigs ) )
            {
                contentData.add( property );
            }

            return this;
        }

        public boolean isChanges()
        {
            return this.changes.isChanges();
        }

        public Changes getChanges()
        {
            return this.changes.build();
        }


        public Site build()
        {
            return new Site( this );
        }

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
