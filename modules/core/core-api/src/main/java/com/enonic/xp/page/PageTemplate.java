package com.enonic.xp.page;


import java.util.List;
import java.util.stream.Collectors;

import com.google.common.annotations.Beta;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNames;

@Beta
public final class PageTemplate
    extends Content
{
    private final PageTemplateKey key;

    private PageTemplate( final Builder builder )
    {
        super( builder );
        this.key = builder.key;
    }

    public PageTemplateKey getKey()
    {
        return key;
    }

    public PropertyTree getConfig()
    {
        return getPage().getConfig();
    }

    public DescriptorKey getController()
    {
        if ( this.getPage() == null )
        {
            return null;
        }
        return this.getPage().getController();
    }

    public ContentTypeNames getCanRender()
    {
        final List<ContentTypeName> list =
            this.getData().getProperties( "supports" ).stream().filter( property -> !property.hasNullValue() ).map(
                property -> ContentTypeName.from( property.getString() ) ).collect( Collectors.toList() );
        return ContentTypeNames.from( list );
    }

    public boolean canRender( ContentTypeName name )
    {
        return this.getCanRender().contains( name );
    }

    public boolean hasRegions()
    {
        return getPage().hasRegions();
    }

    public PageRegions getRegions()
    {
        return getPage().getRegions();
    }

    public static PageTemplate.Builder newPageTemplate()
    {
        return new Builder();
    }

    public static PageTemplate.Builder copyOf( final PageTemplate pageTemplate )
    {
        return new Builder( pageTemplate );
    }

    public static class Builder
        extends Content.Builder<Builder>
    {
        private PageTemplateKey key;

        private Builder()
        {
            super();
        }

        public Builder( final PageTemplate source )
        {
            super( source );
        }

        public Builder key( final PageTemplateKey value )
        {
            this.key = value;
            this.id = value.getContentId();
            return this;
        }

        @Override
        public Builder id( final ContentId value )
        {
            super.id( value );
            this.key = PageTemplateKey.from( value );
            return this;
        }

        public Builder controller( final DescriptorKey descriptorKey )
        {
            if ( this.page == null )
            {
                this.page = Page.create().
                    controller( descriptorKey ).
                    build();
            }
            else
            {
                this.page = Page.create( this.page ).
                    controller( descriptorKey ).
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

        public Builder regions( final PageRegions value )
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
