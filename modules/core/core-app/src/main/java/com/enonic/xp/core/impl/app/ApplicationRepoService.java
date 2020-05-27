package com.enonic.xp.core.impl.app;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.Nodes;

interface ApplicationRepoService
{
    Node createApplicationNode( final Application application, final ByteSource source );

    Node updateApplicationNode( final Application application, final ByteSource source );

    void deleteApplicationNode( final ApplicationKey application );

    Node getApplicationNode( final ApplicationKey applicationKey );

    ByteSource getApplicationSource( final NodeId nodeId );

    Nodes getApplications();

    Node updateStartedState( final ApplicationKey applicationKey, final boolean started );

}
