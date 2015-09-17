package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.Contents;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.Nodes;

public interface ContentNodeTranslator
{
    Contents fromNodes( final Nodes nodes, final boolean resolveHasChildren );

    Content fromNode( final Node node, final boolean resolveHasChildren );
}
