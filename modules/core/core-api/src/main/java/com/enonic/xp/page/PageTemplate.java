package com.enonic.xp.page;


import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.content.Content;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.region.Regions;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNames;

@PublicApi
public final class PageTemplate
    extends Content
{
    private PageTemplate( final Builder builder )
    {
        super( builder );
    }

    public PageTemplateKey getKey()
    {
        return PageTemplateKey.from( getId() );
    }

    public DescriptorKey getController()
    {
        if ( this.getPage() == null )
        {
            return null;
        }
        return this.getPage().getDescriptor();
    }

    public ContentTypeNames getCanRender()
    {
        return this.getData().getProperties( "supports" ).stream().filter( property -> !property.hasNullValue() ).map(
                property -> ContentTypeName.from( property.getString() ) ).collect( ContentTypeNames.collector() );
    }

    public boolean canRender( ContentTypeName name )
    {
        return this.getCanRender().contains( name );
    }

    public static PageTemplate.Builder newPageTemplate()
    {
        return new Builder();
    }

    public static PageTemplate.Builder copyOf( final PageTemplate pageTemplate )
    {
        return new Builder( pageTemplate );
    }

    public static final class Builder
        extends Content.Builder<Builder>
    {
        private Builder()
        {
            super();
            type = ContentTypeName.pageTemplate();
        }

        public Builder( final PageTemplate source )
        {
            super( source );
        }

        public Builder key( final PageTemplateKey value )
        {
            this.id = value.getContentId();
            return this;
        }

        public Builder controller( final DescriptorKey descriptorKey )
        {
            if ( this.page == null )
            {
                this.page = Page.create().
                    descriptor( descriptorKey ).
                    build();
            }
            else
            {
                this.page = Page.create( this.page ).
                    descriptor( descriptorKey ).
                    build();
            }
            return this;
        }

        public Builder canRender( final ContentTypeNames names )
        {
            for ( ContentTypeName name : names )
            {
                this.data.addProperty( "supports", ValueFactory.newString( name.toString() ) );
            }
            return this;
        }

        public Builder regions( final Regions value )
        {
            if ( this.page == null )
            {
                this.page = Page.create().
                    regions( value ).
                    build();
            }
            else
            {
                this.page = Page.create( this.page ).
                    regions( value ).
                    build();
            }

            return this;
        }

        public Builder config( final PropertyTree config )
        {
            if ( this.page == null )
            {
                this.page = Page.create().
                    config( config ).
                    build();
            }
            else
            {
                this.page = Page.create( this.page ).
                    config( config ).
                    build();
            }

            return this;
        }

        @Override
        public PageTemplate build()
        {
            return new PageTemplate( this );
        }

    }

}
