package com.enonic.wem.api.content.page;


import java.util.ArrayList;
import java.util.List;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;

public final class PageTemplate
    extends Content
{
    private final PageTemplateKey key;

    private final PageTemplateName pageTemplateName;

    private PageTemplate( final Builder builder )
    {
        super( builder );
        this.key = builder.key;
        this.pageTemplateName = PageTemplateName.from( super.getName() );
    }

    public PageTemplateKey getKey()
    {
        return key;
    }

    public PageTemplateName getName()
    {
        return this.pageTemplateName;
    }

    public RootDataSet getConfig()
    {
        return getPage().getConfig();
    }

    public PageDescriptorKey getController()
    {
        if ( this.getPage() == null )
        {
            return null;
        }
        return this.getPage().getController();
    }

    public ContentTypeNames getCanRender()
    {
        final List<Property> supports = this.getContentData().getPropertiesByName( "supports" );
        final ArrayList<ContentTypeName> list = new ArrayList<>( supports.size() );
        for ( final Property property : supports )
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

        private Builder( final PageTemplate source )
        {
            super( source );
        }

        public Builder key( final PageTemplateKey value )
        {
            this.key = value;
            this.contentId = value;
            return this;
        }

        public Builder controller( final PageDescriptorKey descriptorKey )
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
                this.contentData.addProperty( "supports", Value.newString( name.toString() ) );
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

        public Builder config( final RootDataSet config )
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
