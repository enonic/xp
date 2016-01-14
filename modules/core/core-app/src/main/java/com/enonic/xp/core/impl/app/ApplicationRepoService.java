package com.enonic.xp.core.impl.app;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.Application;
import com.enonic.xp.node.Node;

interface ApplicationRepoService
{
    Node createApplicationNode( final Application application, final ByteSource source );

    Node updateApplicationNode( final Application application, final ByteSource source );
}
