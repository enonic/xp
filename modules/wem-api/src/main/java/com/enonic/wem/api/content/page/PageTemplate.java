package com.enonic.wem.api.content.page;


import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.support.Changes;
import com.enonic.wem.api.support.EditBuilder;

import static com.enonic.wem.api.support.PossibleChange.newPossibleChange;

public final class PageTemplate
    extends Template<PageTemplateName>
{
    private final ContentTypeNames canRender;

    private PageTemplate( final Builder builder )
    {
        super( builder );
        this.canRender = builder.canRender != null ? builder.canRender : ContentTypeNames.empty();
    }

    private PageTemplate( final PageTemplateProperties properties )
    {
        super( properties );
        this.canRender = properties.canRender != null ? properties.canRender : ContentTypeNames.empty();
    }

    public ContentTypeNames getCanRender()
    {
        return canRender;
    }

    public static PageTemplate.Builder newPageTemplate()
    {
        return new Builder();
    }

    public static class Builder
        extends BaseTemplateBuilder<Builder, PageTemplateName>
    {
        private ContentTypeNames canRender;

        private Builder()
        {
        }

        public Builder canRender( final ContentTypeNames canRender )
        {
            this.canRender = canRender;
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
        private ContentTypeNames canRender;

    }

    public static PageTemplateEditBuilder editPageTemplate( final PageTemplate toBeEdited )
    {
        return new PageTemplateEditBuilder( toBeEdited );
    }

    public static class PageTemplateEditBuilder
        extends PageTemplateProperties
        implements EditBuilder<PageTemplate>
    {
        private ContentTypeNames canRender;

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

        public PageTemplateEditBuilder descriptor( final ModuleResourceKey value )
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
