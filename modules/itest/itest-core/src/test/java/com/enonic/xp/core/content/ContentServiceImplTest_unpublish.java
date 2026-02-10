package com.enonic.xp.core.content;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.content.UnpublishContentParams;
import com.enonic.xp.content.WorkflowState;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.verify;

class ContentServiceImplTest_unpublish
    extends AbstractContentServiceTest
{

    @Test
    void unpublish()
    {
        final Content content = this.contentService.create( CreateContentParams.create()
                                                                .contentData( new PropertyTree() )
                                                                .displayName( "This is my content" )
                                                                .parent( ContentPath.ROOT )
                                                                .type( ContentTypeName.folder() )
                                                                .build() );

        this.contentService.publish( PushContentParams.create().contentIds( ContentIds.from( content.getId() ) ).build() );

        final Context masterContext = ContextBuilder.from( ContextAccessor.current() ).branch( ContentConstants.BRANCH_MASTER ).build();

        assertTrue( masterContext.callWith( () -> contentService.contentExists( content.getId() ) ) );

        this.contentService.unpublish( UnpublishContentParams.create().contentIds( ContentIds.from( content.getId() ) ).build() );

        assertNotNull( contentService.contentExists( content.getId() ) );
        assertFalse( masterContext.callWith( () -> contentService.contentExists( content.getId() ) ) );
        final Content unpublishedContent = this.contentService.getById( content.getId() );
        assertNull( unpublishedContent.getPublishInfo().getFrom() );
        assertNull( unpublishedContent.getPublishInfo().getTo() );
        assertNotNull( unpublishedContent.getPublishInfo().getFirst() );
        assertEquals( WorkflowState.READY, unpublishedContent.getWorkflowInfo().getState() );
    }

    @Test
    void unpublish_with_children()
    {

        final Content content = this.contentService.create( CreateContentParams.create()
                                                                .contentData( new PropertyTree() )
                                                                .displayName( "This is my content" )
                                                                .parent( ContentPath.ROOT )
                                                                .type( ContentTypeName.folder() )
                                                                .build() );

        final Content child = this.contentService.create( CreateContentParams.create()
                                                              .contentData( new PropertyTree() )
                                                              .displayName( "This is my content" )
                                                              .parent( content.getPath() )
                                                              .type( ContentTypeName.folder() )
                                                              .build() );

        this.contentService.publish( PushContentParams.create().contentIds( ContentIds.from( content.getId() ) ).build() );

        final Context masterContext = ContextBuilder.from( ContextAccessor.current() ).branch( ContentConstants.BRANCH_MASTER ).build();

        assertTrue( masterContext.callWith( () -> contentService.contentExists( content.getId() ) ) );
        assertTrue( masterContext.callWith( () -> contentService.contentExists( child.getId() ) ) );

        this.contentService.unpublish( UnpublishContentParams.create().contentIds( ContentIds.from( content.getId() ) ).build() );

        assertNotNull( contentService.contentExists( content.getId() ) );
        assertNotNull( contentService.contentExists( child.getId() ) );
        assertFalse( masterContext.callWith( () -> contentService.contentExists( content.getId() ) ) );
        assertFalse( masterContext.callWith( () -> contentService.contentExists( child.getId() ) ) );
    }

    @Test
    void audit_data()
    {
        final ArgumentCaptor<LogAuditLogParams> captor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        final Content content = this.contentService.create( CreateContentParams.create()
                                                                .contentData( new PropertyTree() )
                                                                .displayName( "This is my content" )
                                                                .parent( ContentPath.ROOT )
                                                                .type( ContentTypeName.folder() )
                                                                .build() );

        this.contentService.publish( PushContentParams.create().contentIds( ContentIds.from( content.getId() ) ).build() );

        final Context masterContext = ContextBuilder.from( ContextAccessor.current() ).branch( ContentConstants.BRANCH_MASTER ).build();

        assertTrue( masterContext.callWith( () -> contentService.contentExists( content.getId() ) ) );

        Mockito.reset( auditLogService );

        this.contentService.unpublish( UnpublishContentParams.create().contentIds( ContentIds.from( content.getId() ) ).build() );

        verify( auditLogService, atMostOnce() ).log( captor.capture() );

        final LogAuditLogParams log = captor.getValue();
        assertThat( log.getType() ).isEqualTo( "system.content.unpublishContent" );
        assertThat( log.getData().getString( "result.unpublishedContents" ) ).isEqualTo( content.getId().toString() );
    }
}
