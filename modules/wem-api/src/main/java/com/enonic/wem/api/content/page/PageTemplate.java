package com.enonic.wem.api.content.page;


import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.support.Changes;
import com.enonic.wem.api.support.EditBuilder;

import static com.enonic.wem.api.support.PossibleChange.newPossibleChange;

public final class PageTemplate
    extends Template<PageTemplateName, PageTemplateKey, PageDescriptorKey>
{
    private final PageRegions regions;

    private final ContentTypeNames canRender;

    private PageTemplate( final Builder builder )
    {
        super( builder );
        this.canRender = builder.canRender != null ? builder.canRender : ContentTypeNames.empty();
        this.regions = builder.regions;
    }

    private PageTemplate( final PageTemplateProperties properties )
    {
        super( properties );
        this.canRender = properties.canRender != null ? properties.canRender : ContentTypeNames.empty();
        this.regions = properties.regions;
    }

    @Override
    protected PageTemplateKey createKey( final ModuleName moduleName, final PageTemplateName name )
    {
        return PageTemplateKey.from( moduleName, name );
    }

    public ContentTypeNames getCanRender()
    {
        return canRender;
    }

    public boolean hasRegions()
    {
        return regions != null;
    }

    public PageRegions getRegions()
    {
        return regions;
    }

    public static PageTemplate.Builder newPageTemplate()
    {
        return new Builder();
    }

    public static class Builder
        extends BaseTemplateBuilder<Builder, PageTemplate, PageTemplateName, PageTemplateKey, PageDescriptorKey>
    {
        private ContentTypeNames canRender;

        private PageRegions regions;

        private Builder()
        {
        }

        public Builder canRender( final ContentTypeNames canRender )
        {
            this.canRender = canRender;
            return this;
        }

        public Builder regions( final PageRegions value )
        {
            this.regions = value;
            return this;
        }

        public PageTemplate build()
        {
            return new PageTemplate( this );
        }
    }

    public static class PageTemplateProperties
        extends TemplateProperties
    {
        ContentTypeNames canRender;

        PageRegions regions;

    }

    public static PageTemplateEditBuilder editPageTemplate( final PageTemplate toBeEdited )
    {
        return new PageTemplateEditBuilder( toBeEdited );
    }

    public static class PageTemplateEditBuilder
        extends PageTemplateProperties
        implements EditBuilder<PageTemplate>
    {

        private final PageTemplate original;

        private final Changes.Builder changes = new Changes.Builder();

        private PageTemplateEditBuilder( PageTemplate original )
        {
            this.original = original;
        }

        public PageTemplateEditBuilder displayName( final String value )
        {
            changes.recordChange( newPossibleChange( "displayName" ).from( this.original.getDisplayName() ).to( value ).build() );
            this.displayName = value;
            return this;
        }

        public PageTemplateEditBuilder descriptor( final PageDescriptorKey value )
        {
            changes.recordChange( newPossibleChange( "descriptor" ).from( this.original.getDescriptor() ).to( value ).build() );
            this.descriptor = value;
            return this;
        }

        public PageTemplateEditBuilder config( final RootDataSet value )
        {
            changes.recordChange( newPossibleChange( "config" ).from( this.original.getConfig() ).to( value ).build() );
            this.config = value;
            return this;
        }

        public PageTemplateEditBuilder canRender( final ContentTypeNames value )
        {
            changes.recordChange( newPossibleChange( "canRender" ).from( this.original.getCanRender() ).to( value ).build() );
            this.canRender = value;
            return this;
        }

        public PageTemplateEditBuilder regions( final PageRegions value )
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

        public PageTemplate build()
        {
            return new PageTemplate( this );
        }
    }
}
