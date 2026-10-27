package com.enonic.xp.core.impl.app;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.Nodes;

public interface ApplicationRepoService
{
    Node upsertApplicationNode( AppInfo application, ByteSource source );

    void deleteApplicationNode( ApplicationKey application );

    Node getApplicationNode( ApplicationKey applicationKey );

    ByteSource getApplicationSource( NodeId nodeId );

    Nodes getApplications();

    Node updateStartedState( ApplicationKey applicationKey, boolean started );
}
