package com.enonic.xp.lib.node;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;

import static com.google.common.base.Strings.isNullOrEmpty;

public class NodeKey
{
    private final boolean isId;

    private final String value;

    private String versionId;

    public static NodeKey from( final String value )
    {
        if ( isNullOrEmpty( value ) )
        {
            return null;
        }

        return new NodeKey( value );
    }

    public static NodeKey from( final String value, final String versionId)
    {
        if ( isNullOrEmpty( value ) )
        {
            return null;
        }

        return new NodeKey( value, versionId);
    }

    private NodeKey(final String value )
    {
        this.isId = !value.startsWith( "/" );
        this.value = value;
    }

    private NodeKey( final String value, final String versionId )
    {
        this( value );
        this.versionId = versionId;
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

        return new NodePath( this.value );
    }

    public boolean isId()
    {
        return isId;
    }

    public boolean isPath()
    {
        return !isId;
    }

    public String getValue()
    {
        return value;
    }

    public String getVersionId()
    {
        return versionId;
    }

    @Override
    public String toString()
    {
        if ( isNullOrEmpty( versionId ) )
        {
            return value;
        }
        return "key=" + value + ", versionId=" + versionId;
    }
}



