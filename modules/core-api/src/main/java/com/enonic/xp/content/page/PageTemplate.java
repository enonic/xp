package com.enonic.xp.content.page;


import java.util.ArrayList;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.Value;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNames;

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
        final ArrayList<ContentTypeName> list = new ArrayList<>( this.getData().countNames( "supports" ) );
        for ( final Property property : this.getData().getProperties( "supports" ) )
        {
            if ( !property.hasNullValue() )
            {
                list.add( ContentTypeName.from( property.getString() ) );
            }
        }
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

    public static PageTemplate.Builder newPageTemplate( final PageTemplate source )
    {
        return new Builder( source );
    }

    public static PageTemplate.Builder copyOf( final PageTemplate pageTemplate )
    {
        return new Builder( pageTemplate );
    }

    public static class Builder
        extends Content.Builder<Builder, PageTemplate>
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
            this.id = value;
            return this;
        }

        @Override
        public Content.Builder<Builder, PageTemplate> id( final ContentId value )
        {
            super.id( value );
            this.key = PageTemplateKey.from( value );
            return this;
        }

        public Builder controller( final DescriptorKey descriptorKey )
        {
            if ( this.page == null )
            {
                this.page = Page.newPage().
                    controller( descriptorKey ).
                    build();
            }
            else
            {
                this.page = Page.newPage( this.page ).
                    controller( descriptorKey ).
                    build();
            }
            return this;
        }

        public Builder canRender( final ContentTypeNames names )
        {
            for ( ContentTypeName name : names )
            {
                this.data.addProperty( "supports", Value.newString( name.toString() ) );
            }
            return this;
        }

        public Builder regions( final PageRegions value )
        {
            if ( this.page == null )
            {
                this.page = Page.newPage().
                    regions( value ).
                    build();
            }
            else
            {
                this.page = Page.newPage( this.page ).
                    regions( value ).
                    build();
            }

            return this;
        }

        public Builder config( final PropertyTree config )
        {
            if ( this.page == null )
            {
                this.page = Page.newPage().
                    config( config ).
                    build();
            }
            else
            {
                this.page = Page.newPage( this.page ).
                    config( config ).
                    build();
            }

            return this;
        }

        public PageTemplate build()
        {
            return new PageTemplate( this );
        }

    }

}
