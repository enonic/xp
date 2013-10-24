package com.enonic.wem.api.item;


import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.Path;

public class NodePath
    extends Path<NodePath>
{
    public final static NodePath ROOT = new NodePath( new Builder().absolute( true ).trailingDivider( false ) );

    private static final char ELEMENT_DIVIDER = '/';

    public NodePath( final String path )
    {
        super( newPath( path ) );
    }

    public NodePath( final NodePath parent, final String element )
    {
        super( newPath( parent ).addElement( element ) );
    }

    public NodePath( final Builder builder )
    {
        super( builder );
    }

    public NodePath asRelative()
    {
        if ( isRelative() )
        {
            return this;
        }
        return newNodePath( this ).absolute( false ).build();
    }

    public NodePath getParentPath()
    {
        return new NodePath( new Builder( this ).removeLastElement() );
    }

    public static Builder newPath()
    {
        return new Builder();
    }

    public static Builder newNodePath( final NodePath source )
    {
        return new Builder( source );
    }

    public static Builder newNodePath( final NodePath parent, final String name )
    {
        return new Builder( parent ).addElement( name );
    }

    public Element getElement( int index )
    {
        return (Element) super.getElement( index );
    }

    public List<NodePath> getParentPaths()
    {
        List<NodePath> parentPaths = new ArrayList<>();

        final Builder builder = new Builder( this );
        for ( int i = 0; i < elementCount(); i++ )
        {
            builder.removeLastElement();
            parentPaths.add( builder.build() );
        }

        return parentPaths;
    }

    public static Builder newPath( final NodePath source )
    {
        Preconditions.checkNotNull( source, "source to build copy from not given" );
        return new Builder( source );
    }

    public static NodePath.Builder newPath( final String path )
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
        extends Path.Builder<NodePath.Builder>
    {
        public Builder()
        {

        }

        public Builder( final NodePath source )
        {
            super( source );
        }

        public NodePath build()
        {
            return new NodePath( this );
        }
    }
}
