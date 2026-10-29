package com.enonic.xp.core.impl.app;

import java.util.Map;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.icon.IconUpdate;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.Nodes;

/**
 * The application nodes in the application repository, laid out as {@link SchemaNodeLayout} describes: the application node holds
 * the manifest, the bundle, the application descriptor and its icon; the {@code cms} node below it holds the schema the
 * application owns.
 */
public interface ApplicationRepoService
{
    Node upsertApplicationNode( AppInfo application, ByteSource source );

    /**
     * Creates the node of an application that has no bundle: it carries nothing but its modified time, the descriptor
     * persisted on it is what the application consists of. Fails if the node exists.
     */
    Node createApplicationNode( ApplicationKey applicationKey );

    void deleteApplicationNode( ApplicationKey application );

    /**
     * Stores the schema resources of the application: the application descriptor ({@code enonic.yaml}) and its icon on the
     * application node, the {@code cms} subtree below it. A previously persisted schema is replaced.
     *
     * @param applicationKey application key
     * @param resources      schema resources as {@link AppSchemaResolver} returns them: paths relative to the application root
     *                       mapped to resource content
     */
    void persistApplicationSchema( ApplicationKey applicationKey, Map<String, ByteSource> resources );

    /**
     * {@code true} when the application descriptor ({@code enonic.yaml}) is persisted on the application node.
     */
    boolean hasApplicationDescriptor( ApplicationKey applicationKey );

    /**
     * {@code true} when the cms descriptor ({@code cms/cms.yaml}) is persisted: the application owns a schema, and is a schema
     * application unless its bundle ships business logic.
     */
    boolean hasCmsDescriptor( ApplicationKey applicationKey );

    /**
     * Writes the application descriptor ({@code enonic.yaml}) on the application node, which must exist. The icon attached to it
     * is kept, removed or replaced as told by {@code iconUpdate}.
     *
     * @return the application node as written
     */
    Node upsertApplicationDescriptor( ApplicationKey applicationKey, String descriptor, IconUpdate iconUpdate );

    Node getApplicationNode( ApplicationKey applicationKey );

    ByteSource getApplicationSource( NodeId nodeId );

    Nodes getApplications();

    Node updateStartedState( ApplicationKey applicationKey, boolean started );
}
