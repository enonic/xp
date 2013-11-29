package com.enonic.wem.api.entity;


import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.BasePath2;

public class NodePath
    extends BasePath2<NodePath,NodePath.Element,NodePath.Builder>
{
    public final static NodePath ROOT = new NodePath( "/" );

    private static final char ELEMENT_DIVIDER = '/';

    public NodePath( final String path )
    {
        super( new Builder( path ) );
    }

    public NodePath( final NodePath parent, final String element )
    {
        super( new Builder( parent ).addElement( element ) );
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

    public NodePath asAbsolute()
    {
        if ( isAbsolute() )
        {
            return this;
        }
        final Builder builder = newNodePath( this );
        return builder.absolute( true ).build();
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

    protected Builder newBuilder()
    {
        return new Builder();
    }

    @Override
    protected Builder newBuilder( final NodePath source )
    {
        return new Builder( source );
    }

    public static Builder newPath( final NodePath source )
    {
        Preconditions.checkNotNull( source, "source to build copy from not given" );
        return new Builder( source );
    }

    public static Builder newPath( final String path )
    {
        final Builder builder = new Builder();
        builder.elementDivider( ELEMENT_DIVIDER );
        builder.elements( path );
        return builder;
    }

    public static class Element
        extends BasePath2.Element
    {
        public Element( final String name )
        {
            super( name );
        }
    }

    public static class Builder
        extends BasePath2.Builder<Builder,NodePath>
    {
        public Builder()
        {

        }

        public Builder( final NodePath source )
        {
            super( source );
        }

        public Builder( final String path )
        {
            super( path, ELEMENT_DIVIDER );
        }

        protected Element newElement( String value )
        {
            return new Element( value );
        }

        public NodePath build()
        {
            return new NodePath( this );
        }
    }
}
