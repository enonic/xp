package com.enonic.xp.core.content;

import java.time.Instant;
import java.util.SplittableRandom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.SortContentParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.core.impl.content.ContentEventsSyncParams;
import com.enonic.xp.core.impl.content.ContentSyncEventType;
import com.enonic.xp.core.impl.content.ParentContentSynchronizer;
import com.enonic.xp.core.internal.orderkey.OrderKeyCodec;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.UpdateNodeParams;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderKeySyncTest
    extends AbstractContentSynchronizerTest
{
    private ParentContentSynchronizer synchronizer;

    @BeforeEach
    void init()
    {
        synchronizer = new ParentContentSynchronizer( this.layersContentService );
    }

    @Test
    void created_content_carries_the_placement_of_its_source()
    {
        final Content sourceParent = projectContext.callWith( () -> createContent( ContentPath.ROOT ) );
        final Content sourceChild = projectContext.callWith( () -> createContent( sourceParent.getPath() ) );

        syncCreated( sourceParent.getId() );
        syncCreated( sourceChild.getId() );

        assertEquals( orderKeyIn( projectContext, sourceChild.getId() ), orderKeyIn( layerContext, sourceChild.getId() ),
                      "an inherited content holds the key it holds in the project it comes from" );
        assertEquals( orderKeyIn( layerContext, sourceChild.getId() ),
                      layerContext.callWith( () -> layersContentService.getById( sourceChild.getId() ).orElseThrow().getOrderKey() ),
                      "the content read surface shows the same key the node holds" );
    }

    @Test
    void reorder_in_the_source_project_travels_to_the_layer()
    {
        final Content sourceParent = projectContext.callWith( () -> createContent( ContentPath.ROOT ) );
        final Content childA = projectContext.callWith( () -> createContent( sourceParent.getPath(), "child-a" ) );
        final Content childB = projectContext.callWith( () -> createContent( sourceParent.getPath(), "child-b" ) );

        syncCreated( sourceParent.getId() );
        syncCreated( childA.getId() );
        syncCreated( childB.getId() );

        projectContext.runWith( () -> contentService.sort(
            SortContentParams.create().contentId( sourceParent.getId() ).childOrder( ChildOrder.orderKeyOrder() ).build() ) );
        final Content sortedTarget = syncSorted( sourceParent.getId() );
        assertEquals( ChildOrder.orderKeyOrder(), sortedTarget.getChildOrder() );

        // move child-a to the top in the source project: a fresh key at a later instant sorts before everything
        final String liftedKey = new OrderKeyCodec( new SplittableRandom( 42 ) )
            .initial( Instant.now().plusSeconds( 60 ), childA.getId().toString() );
        projectContext.runWith( () -> nodeService.update( UpdateNodeParams.create()
                                                              .id( NodeId.from( childA.getId() ) )
                                                              .editor( toBeEdited -> toBeEdited.orderKey = liftedKey )
                                                              .build() ) );

        assertEquals( liftedKey, orderKeyIn( projectContext, childA.getId() ), "the lift must stick in the source first" );

        syncUpdated( childA.getId() );

        final String layerKeyA = orderKeyIn( layerContext, childA.getId() );
        final String layerKeyB = orderKeyIn( layerContext, childB.getId() );
        assertEquals( liftedKey, layerKeyA, "the reordered child holds the same key in the layer" );
        assertTrue( layerKeyA.compareTo( layerKeyB ) < 0, "the layer shows the source order: lifted child first" );
    }

    @Test
    void keyless_source_dictates_nothing()
    {
        final Content sourceParent = projectContext.callWith( () -> createContent( ContentPath.ROOT ) );
        final Content sourceChild = projectContext.callWith( () -> createContent( sourceParent.getPath() ) );

        syncCreated( sourceParent.getId() );
        syncCreated( sourceChild.getId() );

        // strip the key in the source, the state of a node stored before order keys existed
        projectContext.runWith( () -> nodeService.update( UpdateNodeParams.create()
                                                              .id( NodeId.from( sourceChild.getId() ) )
                                                              .editor( toBeEdited -> toBeEdited.orderKey = null )
                                                              .build() ) );

        final String targetKeyBefore = orderKeyIn( layerContext, sourceChild.getId() );
        syncUpdated( sourceChild.getId() );

        assertNotNull( targetKeyBefore );
        assertEquals( targetKeyBefore, orderKeyIn( layerContext, sourceChild.getId() ),
                      "a keyless source leaves the layer's placement alone" );
    }

    private String orderKeyIn( final Context context, final ContentId contentId )
    {
        return context.callWith( () -> nodeService.getById( NodeId.from( contentId ) ).getOrderKey() );
    }

    private void syncCreated( final ContentId contentId )
    {
        synchronizer.sync( ContentEventsSyncParams.create()
                               .addContentId( contentId )
                               .sourceProject( project.getName() )
                               .targetProject( layer.getName() )
                               .syncEventType( ContentSyncEventType.CREATED )
                               .build() );
    }

    private void syncUpdated( final ContentId contentId )
    {
        synchronizer.sync( ContentEventsSyncParams.create()
                               .addContentId( contentId )
                               .sourceProject( project.getName() )
                               .targetProject( layer.getName() )
                               .syncEventType( ContentSyncEventType.UPDATED )
                               .build() );
    }

    private Content syncSorted( final ContentId contentId )
    {
        synchronizer.sync( ContentEventsSyncParams.create()
                               .addContentId( contentId )
                               .sourceProject( project.getName() )
                               .targetProject( layer.getName() )
                               .syncEventType( ContentSyncEventType.SORTED )
                               .build() );
        return layerContext.callWith( () -> layersContentService.getById( contentId ).orElseThrow() );
    }
}
