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

            this.rootNode = nodeService.getByPath( NodePath.create( NodePath.ROOT, "rootNode" ).build() );
            this.nonPublishedNodesRoot = nodeService.getByPath( NodePath.create( NodePath.ROOT, "rootNode/nonPublishedRoot" ).build() );
            this.halfPublishedNodesRoot = nodeService.getByPath( NodePath.create( NodePath.ROOT, "rootNode/halfPublishedRoot" ).build() );
            this.publishedNodesRoot = nodeService.getByPath( NodePath.create( NodePath.ROOT, "rootNode/publishedRoot" ).build() );
            this.publishedDynamicRoot = nodeService.getByPath( NodePath.create( NodePath.ROOT, "rootNode/publishedDynamicRoot" ).build() );

            return 1;
        } );

    }

    public void teardown()
        throws Exception
    {
        stopClient();
    }
}

