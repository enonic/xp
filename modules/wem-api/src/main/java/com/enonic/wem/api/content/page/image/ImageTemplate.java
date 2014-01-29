package com.enonic.wem.api.content.page.image;


import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.page.Template;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.support.Changes;
import com.enonic.wem.api.support.EditBuilder;

import static com.enonic.wem.api.support.PossibleChange.newPossibleChange;

public final class ImageTemplate
    extends Template<ImageTemplateName, ImageTemplateKey, ImageDescriptorKey>
{
    private ContentId image;

    private ImageTemplate( final ImageTemplateProperties properties )
    {
        super( properties );
        this.image = properties.image;
    }

    @Override
    protected ImageTemplateKey createKey( final ModuleName moduleName, final ImageTemplateName name )
    {
        return ImageTemplateKey.from( moduleName, name );
    }

    private ImageTemplate( final Builder builder )
    {
        super( builder );
        this.image = builder.image;
    }

    public ContentId getImage()
    {
        return image;
    }

    public static Builder newImageTemplate()
    {
        return new Builder();
    }

    public static class Builder
        extends BaseTemplateBuilder<Builder, ImageTemplate, ImageTemplateName, ImageTemplateKey, ImageDescriptorKey>
    {
        private ContentId image;

        private Builder()
        {
        }

        @Override
        public Builder name( final String name )
        {
            this.name = ImageTemplateName.from( name );
            return this;
        }

        public Builder image( final ContentId value )
        {
            this.image = value;
            return this;
        }

        public ImageTemplate build()
        {
            return new ImageTemplate( this );
        }
    }

    public static class ImageTemplateProperties
        extends TemplateProperties
    {
        ContentId image;
    }

    public static ImageTemplateEditBuilder editImageTemplate( final ImageTemplate toBeEdited )
    {
        return new ImageTemplateEditBuilder( toBeEdited );
    }

    public static class ImageTemplateEditBuilder
        extends ImageTemplateProperties
        implements EditBuilder<ImageTemplate>
    {
        private final ImageTemplate original;

        private final Changes.Builder changes = new Changes.Builder();

        private ImageTemplateEditBuilder( ImageTemplate original )
        {
            this.original = original;
        }

        public ImageTemplateEditBuilder displayName( final String value )
        {
            changes.recordChange( newPossibleChange( "displayName" ).from( this.original.getDisplayName() ).to( value ).build() );
            this.displayName = value;
            return this;
        }

        public ImageTemplateEditBuilder descriptor( final ImageDescriptorKey value )
        {
            changes.recordChange( newPossibleChange( "descriptor" ).from( this.original.getDescriptor() ).to( value ).build() );
            this.descriptor = value;
            return this;
        }

        public ImageTemplateEditBuilder config( final RootDataSet value )
        {
            changes.recordChange( newPossibleChange( "config" ).from( this.original.getConfig() ).to( value ).build() );
            this.config = value;
            return this;
        }

        public ImageTemplateEditBuilder image( final ContentId value )
        {
            this.image = value;
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

        public ImageTemplate build()
        {
            return new ImageTemplate( this );
        }
    }
}
