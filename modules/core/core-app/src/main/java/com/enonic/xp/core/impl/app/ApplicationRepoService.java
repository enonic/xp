package com.enonic.xp.core.impl.app;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.Nodes;

interface ApplicationRepoService
{
    Node createApplicationNode( Application application, ByteSource source );

    Node updateApplicationNode( Application application, ByteSource source );

    void deleteApplicationNode( ApplicationKey application );

    Node getApplicationNode( ApplicationKey applicationKey );

    ByteSource getApplicationSource( NodeId nodeId );

    Nodes getApplications();

    Node updateStartedState( ApplicationKey applicationKey, boolean started );

}
