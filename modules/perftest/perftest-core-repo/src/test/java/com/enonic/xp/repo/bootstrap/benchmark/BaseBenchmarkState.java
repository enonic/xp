package com.enonic.xp.repo.bootstrap.benchmark;

import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.bootstrap.PerformanceTestBootstrap;

public abstract class BaseBenchmarkState
    extends PerformanceTestBootstrap
{
    public void setup()
    {
        startClient();
        setupServices();

        CONTEXT_DRAFT.callWith( () -> {

            this.ROOT_NODE = nodeService.getByPath( NodePath.create( NodePath.ROOT, "rootNode" ).build() );
            this.NON_PUBLISHED_NODES_ROOT = nodeService.getByPath( NodePath.create( NodePath.ROOT, "rootNode/nonPublishedRoot" ).build() );
            this.HALF_PUBLISHED_NODES_ROOT =
                nodeService.getByPath( NodePath.create( NodePath.ROOT, "rootNode/halfPublishedRoot" ).build() );
            this.PUBLISHED_NODES_ROOT = nodeService.getByPath( NodePath.create( NodePath.ROOT, "rootNode/publishedRoot" ).build() );
            this.PUBLISHED_DYNAMIC_ROOT =
                nodeService.getByPath( NodePath.create( NodePath.ROOT, "rootNode/publishedDynamicRoot" ).build() );

            return 1;
        } );

    }

    public void teardown()
        throws Exception
    {
        stopClient();
    }
}

