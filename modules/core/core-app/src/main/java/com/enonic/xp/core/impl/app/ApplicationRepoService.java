package com.enonic.xp.core.impl.app;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.Application;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;

interface ApplicationRepoService
{
    Node createApplicationNode( final Application application, final ByteSource source );

    Node updateApplicationNode( final Application application, final ByteSource source );

    Node getApplicationNode( final String applicationName );

    ByteSource getApplicationSource( final NodeId nodeId );

}
