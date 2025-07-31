package com.enonic.xp.node;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;

import com.enonic.xp.annotation.PublicApi;

import static com.google.common.base.Strings.isNullOrEmpty;

@PublicApi
public final class NodePath
    implements Comparable<NodePath>, Serializable
{
    private static final long serialVersionUID = 0;

    private static final String ELEMENT_DIVIDER = "/";

    public static final NodePath ROOT = new NodePath( ELEMENT_DIVIDER, false );

    private final String path;

    private NodePath( final String path, boolean ignore )
    {
        this.path = path;
    }

    public NodePath( final String path )
    {
        if ( path.isEmpty() )
        {
            this.path = "";
        }
        else if ( path.equals( ELEMENT_DIVIDER ) )
        {
            this.path = ELEMENT_DIVIDER;
        }
        else
        {
            this.path = toElements( path ).stream()
                .collect( Collectors.joining( ELEMENT_DIVIDER, isAbsolute( path ) ? ELEMENT_DIVIDER : "",
                                              hasTrailing( path ) ? ELEMENT_DIVIDER : "" ) );
        }
    }

    public NodePath( final NodePath parent, final NodeName name )
    {
        if ( name.isRoot() )
        {
            throw new IllegalArgumentException( "Can't add root name to path" );
        }
        final StringBuilder stringBuilder = new StringBuilder( parent.path );
        if ( !parent.path.endsWith( ELEMENT_DIVIDER ) )
        {
            stringBuilder.append( ELEMENT_DIVIDER );
        }
        stringBuilder.append( name );
        this.path = stringBuilder.toString();
    }

    public boolean isRoot()
    {
        return this.path.equals( ELEMENT_DIVIDER );
    }

    public NodePath getParentPath()
    {
        if ( this.path.isEmpty() || this.path.equals( ELEMENT_DIVIDER ) )
        {
            return this;
        }

        final boolean hasTrailing = hasTrailing( this.path );
        final int endIndex = hasTrailing ? this.path.length() - 1 : this.path.length();
        final String stringNoTrailing = this.path.substring( 0, endIndex );

        final int lastDivider = stringNoTrailing.lastIndexOf( ELEMENT_DIVIDER );

        if ( lastDivider == 0 )
        {
            return NodePath.ROOT;
        }
        else if ( lastDivider == -1 )
        {
            return new NodePath( "", false );
        }
        else
        {
            return new NodePath( stringNoTrailing.substring( 0, hasTrailing ? lastDivider + 1 : lastDivider ), false );
        }
    }

    public List<NodePath> getParentPaths()
    {
        List<NodePath> parentPaths = new ArrayList<>();

        if ( isEmpty() )
        {
            return parentPaths;
        }

        NodePath nodePath = this;
        do
        {
            NodePath parent = nodePath.getParentPath();

            parentPaths.add( parent );
            nodePath = parent;
        }
        while ( !nodePath.isEmpty() );

        return parentPaths;
    }

    public boolean isEmpty()
    {
        return this.path.isEmpty() || this.path.equals( ELEMENT_DIVIDER );
    }

    public boolean isAbsolute()
    {
        return isAbsolute( this.path );
    }

    public String getName()
    {
        if ( isEmpty() )
        {
            return null;
        }
        else
        {
            final int stringIndex = isAbsolute( this.path ) ? 1 : 0;
            final int endIndex = hasTrailing( this.path ) ? this.path.length() - 1 : this.path.length();
            final String stringNoTrailing = this.path.substring( stringIndex, endIndex );
            final int beginIndex = stringNoTrailing.lastIndexOf( ELEMENT_DIVIDER );
            return stringNoTrailing.substring( beginIndex == -1 ? 0 : beginIndex + 1 );
        }
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null )
        {
            return false;
        }

        if ( !( o instanceof NodePath ) )
        {
            return false;
        }

        final NodePath path = (NodePath) o;
        return this.path.equalsIgnoreCase( path.path );
    }

    @Override
    public int hashCode()
    {
        return path.toLowerCase().hashCode();
    }

    @Override
    public String toString()
    {
        return path;
    }

    public static Builder create()
    {
        return create( NodePath.ROOT );
    }

    public static Builder create( final NodePath source )
    {
        return new Builder( source );
    }

    @Override
    public int compareTo( final NodePath o )
    {
        return this.path.compareTo( o.path );
    }

    private static List<String> toElements( final String elements )
    {
        return Splitter.on( ELEMENT_DIVIDER ).omitEmptyStrings().trimResults().splitToList( elements );
    }

    private static boolean isAbsolute( final String path )
    {
        return path.startsWith( ELEMENT_DIVIDER );
    }

    private static boolean hasTrailing( final String path )
    {
        return !path.equals( ELEMENT_DIVIDER ) && path.endsWith( ELEMENT_DIVIDER );
    }

    public static final class Builder
    {
        private boolean absolute;

        private boolean trailingDivider;

        private final ArrayList<String> elementListBuilder;

        private Builder( final NodePath source )
        {
            Preconditions.checkNotNull( source, "source to build copy from not given" );
            this.absolute = isAbsolute( source.path );
            this.trailingDivider = hasTrailing( source.path );
            this.elementListBuilder = source.isEmpty() ? new ArrayList<>() : new ArrayList<>( toElements( source.path ) );
        }

        public Builder addElement( final String value )
        {
            if ( isNullOrEmpty( value ) )
            {
                return this;
            }

            this.elementListBuilder.add( NodeName.from( value ).toString() );
            return this;
        }

        public NodePath build()
        {
            return new NodePath( this.elementListBuilder.stream()
                                     .collect( Collectors.joining( ELEMENT_DIVIDER, this.absolute ? ELEMENT_DIVIDER : "",
                                                                   this.trailingDivider && !this.elementListBuilder.isEmpty()
                                                                       ? ELEMENT_DIVIDER
                                                                       : "" ) ), false );
        }
    }
}
