package com.enonic.xp.lib.node;

import com.enonic.xp.node.BinaryAttachments;
import com.enonic.xp.node.NodeEditor;

public class ModifyNodeHandlerParams
{
    private final NodeEditor nodeEditor;

    private final BinaryAttachments attachments;

    public ModifyNodeHandlerParams( final BinaryAttachments attachments, final NodeEditor nodeEditor )
    {
        this.attachments = attachments;
        this.nodeEditor = nodeEditor;
    }

    public NodeEditor getNodeEditor()
    {
        return nodeEditor;
    }

    public BinaryAttachments getAttachments()
    {
        return attachments;
    }
}
