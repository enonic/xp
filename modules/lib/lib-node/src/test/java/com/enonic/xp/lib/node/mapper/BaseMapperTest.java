package com.enonic.xp.lib.node.mapper;

import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.testing.helper.JsonAssert;

public abstract class BaseMapperTest
{
    NodeBranchEntry createEntry( final String a )
    {
        return NodeBranchEntry.create().
            nodeId( NodeId.from( a ) ).
            nodePath( new NodePath( "/" + a ) ).
            build();
    }

    void assertJson( final String fileName, final MapSerializable actualNode )
        throws Exception
    {
        JsonAssert.assertJson( getClass(), fileName, actualNode );
    }
}
