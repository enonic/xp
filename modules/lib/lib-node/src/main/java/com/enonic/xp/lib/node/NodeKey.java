package com.enonic.xp.lib.node;

import com.google.common.base.Strings;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;

public class NodeKey
{
    private final boolean isId;

    private final String value;

    public static NodeKey from( final String value )
    {
        if ( Strings.isNullOrEmpty( value ) )
        {
            return null;
        }

        return new NodeKey( !value.startsWith( "/" ), value );
    }

    private NodeKey( final boolean isId, final String value )
    {
        this.isId = isId;
        this.value = value;
    }

    NodeId getAsNodeId()
    {
        if ( !isId )
        {
            throw new IllegalArgumentException( "Key not of type id" );
        }

        return NodeId.from( this.value );
    }

    NodePath getAsPath()
    {
        if ( isId )
        {
            throw new IllegalArgumentException( "Key not of type path" );
        }

        return NodePath.create( this.value ).build();
    }

    public boolean isId()
    {
        return isId;
    }

    public String getValue()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return value;
    }
}



