package com.enonic.wem.api.content.page.layout;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.page.Template;
import com.enonic.wem.api.content.page.TemplateName;
import com.enonic.wem.api.content.page.image.ImageTemplateKey;
import com.enonic.wem.api.content.page.image.ImageTemplateName;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.support.Changes;
import com.enonic.wem.api.support.EditBuilder;

import static com.enonic.wem.api.support.PossibleChange.newPossibleChange;

public class LayoutTemplate
    extends Template<LayoutTemplateName, LayoutTemplateKey, LayoutDescriptorKey>
{
    private final LayoutRegions regions;

    private LayoutTemplate( final Builder builder )
    {
        super( builder );
        Preconditions.checkNotNull( builder.regions, "regions cannot be null" );
        this.regions = builder.regions;
    }

    private LayoutTemplate( final LayoutTemplateProperties properties )
    {
        super( properties );
        this.regions = properties.regions;
    }

    @Override
    protected LayoutTemplateKey createKey( final SiteTemplateKey siteTemplateKey, final ModuleKey moduleKey, final LayoutTemplateName name )
    {
        return LayoutTemplateKey.from( siteTemplateKey, moduleKey, name );
    }

    public LayoutRegions getRegions()
    {
        return regions;
    }

    public static LayoutTemplate.Builder newLayoutTemplate()
    {
        return new Builder();
    }

    public static class Builder
        extends BaseTemplateBuilder<Builder, LayoutTemplate, LayoutTemplateName, LayoutTemplateKey, LayoutDescriptorKey>
    {
        private LayoutRegions regions;

        private Builder()
        {
        }

        public Builder regions( final LayoutRegions regions )
        {
            this.regions = regions;
            return this;
        }

        public LayoutTemplate build()
        {
            return new LayoutTemplate( this );
        }
    }

    public static class LayoutTemplateProperties
        extends TemplateProperties
    {
        LayoutRegions regions;
    }

    public static LayoutTemplateEditBuilder editLayoutTemplate( final LayoutTemplate toBeEdited )
    {
        return new LayoutTemplateEditBuilder( toBeEdited );
    }

    public static class LayoutTemplateEditBuilder
        extends LayoutTemplateProperties
        implements EditBuilder<LayoutTemplate>
    {
        private final LayoutTemplate original;

        private final Changes.Builder changes = new Changes.Builder();

        private LayoutTemplateEditBuilder( LayoutTemplate original )
        {
            this.original = original;
        }

        public LayoutTemplateEditBuilder displayName( final String value )
        {
            changes.recordChange( newPossibleChange( "displayName" ).from( this.original.getDisplayName() ).to( value ).build() );
            this.displayName = value;
            return this;
        }

        public LayoutTemplateEditBuilder descriptor( final LayoutDescriptorKey value )
        {
            changes.recordChange( newPossibleChange( "descriptor" ).from( this.original.getDescriptor() ).to( value ).build() );
            this.descriptor = value;
            return this;
        }

        public LayoutTemplateEditBuilder config( final RootDataSet value )
        {
            changes.recordChange( newPossibleChange( "config" ).from( this.original.getConfig() ).to( value ).build() );
            this.config = value;
            return this;
        }

        public LayoutTemplateEditBuilder regions( final LayoutRegions value )
        {
            changes.recordChange( newPossibleChange( "regions" ).from( this.original.getRegions() ).to( value ).build() );
            this.regions = value;
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

        public LayoutTemplate build()
        {
            return new LayoutTemplate( this );
        }
    }
}
