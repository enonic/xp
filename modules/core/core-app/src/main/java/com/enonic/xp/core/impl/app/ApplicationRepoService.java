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

    /**
     * Creates the node of an application that has no bundle: it carries nothing but its modified time, the descriptor
     * persisted below it is what the application consists of. Fails if the node exists.
     */
    Node createApplicationNode( ApplicationKey applicationKey );

    void deleteApplicationNode( ApplicationKey application );

    /**
     * Stores schema resources of the application as nodes below the application node ({@code /applications/<name>}):
     * the application descriptor ({@code enonic.yaml}, with the icon {@code enonic.svg} attached to it) and the {@code cms} subtree.
     * A previously persisted schema is replaced.
     *
     * @param applicationKey application key
     * @param resources      schema resources, paths relative to the application root mapped to resource content
     */
    void persistApplicationSchema( ApplicationKey applicationKey, Map<String, ByteSource> resources );

    /**
     * Removes the schema persisted for the application ({@code enonic.yaml} and {@code cms} below the application node), if any.
     */
    void deleteApplicationSchema( ApplicationKey applicationKey );

    /**
     * The persisted application descriptor node ({@code enonic.yaml} below the application node), {@code null} if there is none.
     */
    Node getApplicationDescriptorNode( ApplicationKey applicationKey );

    /**
     * Writes the application descriptor node ({@code enonic.yaml}): created below the application node, which must exist, or
     * updated in place. The icon attached to it is kept, removed or replaced as told by {@code iconUpdate}.
     *
     * @return the written node
     */
    Node upsertApplicationDescriptor( ApplicationKey applicationKey, String descriptor, ApplicationIconUpdate iconUpdate );

    Node getApplicationNode( ApplicationKey applicationKey );

    ByteSource getApplicationSource( NodeId nodeId );

    Nodes getApplications();

    Node updateStartedState( ApplicationKey applicationKey, boolean started );
}
