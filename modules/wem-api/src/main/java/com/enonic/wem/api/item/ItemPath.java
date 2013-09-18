package com.enonic.wem.api.item;


import java.util.ArrayList;
import java.util.List;

import com.enonic.wem.api.Path;

public class ItemPath
    extends Path<ItemPath>
{
    private static final char ELEMENT_DIVIDER = '/';

    public ItemPath( final String path )
    {
        super( newPath( path ) );
    }

    public ItemPath( final ItemPath parent, final String element )
    {
        super( newPath( parent ).addElement( element ) );
    }

    public ItemPath( final Builder builder )
    {
        super( builder );
    }

    public static Builder newPath()
    {
        return new Builder();
    }

    public Element getElement( int index )
    {
        return (Element) super.getElement( index );
    }

    public List<ItemPath> getParentPaths()
    {
        List<ItemPath> parentPaths = new ArrayList<>();

        final Builder builder = new Builder( this );
        for ( int i = 0; i < elementCount(); i++ )
        {
            builder.removeLastElement();
            parentPaths.add( builder.build() );
        }

        return parentPaths;
    }

    public static Builder newPath( final ItemPath source )
    {
        return new Builder( source );
    }

    public static ItemPath.Builder newPath( final String path )
    {
        final Builder builder = new Builder();
        builder.elementDivider( ELEMENT_DIVIDER );
        builder.elements( path );
        return builder;
    }

    public static class Element
        extends Path.Element
    {
        public Element( final String name )
        {
            super( name );
        }
    }

    public static class Builder
        extends Path.Builder<ItemPath.Builder>
    {
        public Builder()
        {

        }

        public Builder( final ItemPath source )
        {
            super( source );
        }

        public ItemPath build()
        {
            return new ItemPath( this );
        }
    }
}
