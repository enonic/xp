package com.enonic.xp.core.impl.app;

import java.util.Map;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.Nodes;

public interface ApplicationRepoService
{
    Node upsertApplicationNode( AppInfo application, ByteSource source );

    void deleteApplicationNode( ApplicationKey application );

    /**
     * Stores schema resources of the application as nodes below the application node ({@code /applications/<name>}):
     * the application descriptor and icon ({@code enonic.yaml}, {@code enonic.svg}) and the {@code cms} subtree.
     * A previously persisted schema is replaced.
     *
     * @param applicationKey application key
     * @param resources      schema resources, paths relative to the application root mapped to resource content
     */
    void persistApplicationSchema( ApplicationKey applicationKey, Map<String, ByteSource> resources );

    /**
     * Removes the schema persisted for the application ({@code enonic.yaml}, {@code enonic.svg} and {@code cms} below the
     * application node), if any.
     */
    void deleteApplicationSchema( ApplicationKey applicationKey );

    Node getApplicationNode( ApplicationKey applicationKey );

    ByteSource getApplicationSource( NodeId nodeId );

    Nodes getApplications();

    Node updateStartedState( ApplicationKey applicationKey, boolean started );
}
