package com.enonic.xp.repo.impl.storage;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeVersionMetadata;

public record NodeVersionData(Node node, NodeVersionMetadata metadata)
{

}
