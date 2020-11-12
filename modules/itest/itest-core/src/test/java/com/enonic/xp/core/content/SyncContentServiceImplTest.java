package com.enonic.xp.core.content;

import java.util.EnumSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ResetContentInheritParams;
import com.enonic.xp.content.SetContentChildOrderParams;
import com.enonic.xp.core.impl.content.ContentEventsSyncParams;
import com.enonic.xp.core.impl.content.ContentSyncEventType;
import com.enonic.xp.core.impl.content.ParentContentSynchronizer;
import com.enonic.xp.core.impl.content.SyncContentServiceImpl;
import com.enonic.xp.index.ChildOrder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SyncContentServiceImplTest
    extends AbstractContentSynchronizerTest
{
    private ParentContentSynchronizer synchronizer;

    private SyncContentServiceImpl syncContentService;

    @BeforeEach
    public void setUpNode()
        throws Exception
    {
        super.setUpNode();

        synchronizer = new ParentContentSynchronizer( contentService, mediaInfoService );

        syncContentService =
            new SyncContentServiceImpl( contentTypeService, nodeService, eventPublisher, pageDescriptorService, partDescriptorService,
                                        layoutDescriptorService, projectService, contentService, synchronizer );
    }

    @Test
    public void testRestoreSort()
        throws Exception
    {
        final Content source = sourceContext.callWith( () -> createContent( ContentPath.ROOT ) );

        syncCreated( source.getId() );

        targetContext.runWith( () -> {
            final Content changed = contentService.setChildOrder( SetContentChildOrderParams.create().
                contentId( source.getId() ).
                childOrder( ChildOrder.from( "_name ASC" ) ).
                build() );

            assertFalse( changed.getInherit().contains( ContentInheritType.SORT ) );
        } );

        syncContentService.resetInheritance( ResetContentInheritParams.create().
            contentId( source.getId() ).
            inherit( EnumSet.of( ContentInheritType.SORT ) ).
            projectName( targetProject.getName() ).
            build() );

        targetContext.runWith( () -> {
            final Content changed = contentService.getById( source.getId() );

            assertTrue( changed.getInherit().contains( ContentInheritType.SORT ) );
        } );

    }

    private Content syncCreated( final ContentId contentId )
    {
        synchronizer.sync( ContentEventsSyncParams.create().
            contentId( contentId ).
            sourceProject( sourceProject.getName() ).
            targetProject( targetProject.getName() ).
            addSyncEventType( ContentSyncEventType.CREATED ).
            build() );

        return targetContext.callWith( () -> contentService.getById( contentId ) );
    }
}