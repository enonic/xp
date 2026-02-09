package com.enonic.xp.node;


import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.base.Splitter;

import com.enonic.xp.annotation.PublicApi;

import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.isNullOrEmpty;

@PublicApi
public final class NodePath
    implements Comparable<NodePath>, Serializable
{
    @Serial
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
        Objects.requireNonNull( emptyToNull( path ), "path not given" );
        if ( path.equals( ELEMENT_DIVIDER ) )
        {
            this.path = ELEMENT_DIVIDER;
        }
        else
        {
            this.path =
                toElements( path ).stream().map( NodeName::toString ).collect( Collectors.joining( ELEMENT_DIVIDER, ELEMENT_DIVIDER, "" ) );
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
        if ( this.path.equals( ELEMENT_DIVIDER ) )
        {
            return this;
        }

        final int endIndex = this.path.lastIndexOf( ELEMENT_DIVIDER );
        if ( endIndex == 0 )
        {
            return ROOT;
        }
        return new NodePath( this.path.substring( 0, endIndex ), false );
    }

    public List<NodePath> getParentPaths()
    {
        List<NodePath> parentPaths = new ArrayList<>();

        if ( isRoot() )
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
        while ( !nodePath.isRoot() );

        return parentPaths;
    }

    public NodeName getName()
    {
        return NodeName.fromInternal( path.substring( this.path.lastIndexOf( ELEMENT_DIVIDER ) + 1 ) );
    }

    @Override
    public boolean equals( final Object o )
    {
        return this == o || o instanceof final NodePath that && this.path.equalsIgnoreCase( that.path );
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

    private static ArrayList<NodeName> toElements( final String elements )
    {
        return Splitter.on( ELEMENT_DIVIDER )
            .omitEmptyStrings()
            .trimResults()
            .splitToStream( elements )
            .map( NodeName::from )
            .collect( Collectors.toCollection( ArrayList::new ) );
    }

    public static final class Builder
    {
        private final ArrayList<NodeName> elementListBuilder;

        private Builder( final NodePath source )
        {
            Objects.requireNonNull( source, "source to build copy from not given" );
            this.elementListBuilder = source.isRoot() ? new ArrayList<>() : toElements( source.path );
        }

        public Builder addElement( final String value )
        {
            if ( isNullOrEmpty( value ) )
            {
                return this;
            }
            this.elementListBuilder.add( NodeName.from( value ) );
            return this;
        }

        public Builder addElement( final NodeName value )
        {
            this.elementListBuilder.add( value );
            return this;
        }

        public NodePath build()
        {
            return new NodePath( this.elementListBuilder.stream()
                                     .map( NodeName::toString )
                                     .collect( Collectors.joining( ELEMENT_DIVIDER, ELEMENT_DIVIDER, "" ) ), false );
        }
    }
}
