package com.enonic.xp.lib.node;

import java.util.ArrayList;
import java.util.List;

public class GetNodeHandlerParams
{

    private List<NodeKey> keys = new ArrayList<>();

    public void add( final String key )
    {
        NodeKey nodeKey = NodeKey.from( key );
        if ( nodeKey != null )
        {
            this.keys.add( nodeKey );
        }
    }

    public void add( final String key, final String versionId )
    {
        NodeKey nodeKey = NodeKey.from( key, versionId );
        if ( nodeKey != null )
        {
            this.keys.add( nodeKey );
        }
    }

    public List<NodeKey> getKeys()
    {
        return keys;
    }

}
