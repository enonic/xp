package com.enonic.xp.repo.impl.storage;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.node.Attributes;

public class StoreNodeParams
{
    private final Node node;

    private final NodeCommitId nodeCommitId;

    private final Attributes versionAttributes;

    private final boolean newVersion;

    private StoreNodeParams( final Node node, final NodeCommitId nodeCommitId, final boolean newVersion, final Attributes versionAttributes )
    {
        this.node = node;
        this.nodeCommitId = nodeCommitId;
        this.newVersion = newVersion;
        this.versionAttributes = versionAttributes;
    }

    public Node getNode()
    {
        return node;
    }

    public NodeCommitId getNodeCommitId()
    {
        return nodeCommitId;
    }

    public Attributes getVersionAttributes()
    {
        return versionAttributes;
    }

    public boolean isNewVersion()
    {
        return newVersion;
    }

    public static StoreNodeParams newVersion( final Node node, final Attributes attributes )
    {
        return new StoreNodeParams( node, null, true, attributes );
    }

    public static StoreNodeParams newVersion( final Node node )
    {
        return new StoreNodeParams( node, null, true, null );
    }

    public static StoreNodeParams overrideVersion( final Node node, final NodeCommitId nodeCommitId, final Attributes attributes )
    {
        return new StoreNodeParams( node, nodeCommitId, false, attributes );
    }
}
