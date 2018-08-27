package com.enonic.xp.node;

public interface DuplicateNodeProcessor
{
    CreateNodeParams process( final NodeId originalNodeId, final CreateNodeParams originalParams );

}
