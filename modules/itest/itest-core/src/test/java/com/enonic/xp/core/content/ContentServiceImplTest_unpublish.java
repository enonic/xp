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
import com.enonic.xp.content.PublishContentParams;
import com.enonic.xp.content.UnpublishContentParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContentServiceImplTest_unpublish
    extends AbstractContentServiceTest
{

    @Test
    public void unpublish()
        throws Exception
    {
        final Content content = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build() );

        this.contentService.publish( PublishContentParams.create().
            target( ContentConstants.BRANCH_MASTER ).
            contentIds( ContentIds.from( content.getId() ) ).
            build() );

        final Context masterContext = ContextBuilder.from( ContextAccessor.current() ).
            branch( ContentConstants.BRANCH_MASTER ).
            build();

        assertTrue( masterContext.callWith( () -> contentService.contentExists( content.getId() ) ) );

        this.contentService.unpublishContent( UnpublishContentParams.create().
            contentIds( ContentIds.from( content.getId() ) ).
            unpublishBranch( ContentConstants.BRANCH_MASTER ).
            build() );

        assertNotNull( contentService.contentExists( content.getId() ) );
        assertFalse( masterContext.callWith( () -> contentService.contentExists( content.getId() ) ) );
        final Content unpublishedContent = this.contentService.getById( content.getId() );
        assertNull( unpublishedContent.getPublishInfo().getFrom() );
        assertNull( unpublishedContent.getPublishInfo().getTo() );
        assertNotNull( unpublishedContent.getPublishInfo().getFirst() );
    }

    @Test
    public void unpublish_with_children()
        throws Exception
    {

        final Content content = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build() );

        final Content child = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( content.getPath() ).
            type( ContentTypeName.folder() ).
            build() );

        this.contentService.publish( PublishContentParams.create().
            target( ContentConstants.BRANCH_MASTER ).
            contentIds( ContentIds.from( content.getId() ) ).
            build() );

        final Context masterContext = ContextBuilder.from( ContextAccessor.current() ).
            branch( ContentConstants.BRANCH_MASTER ).
            build();

        assertTrue( masterContext.callWith( () -> contentService.contentExists( content.getId() ) ) );
        assertTrue( masterContext.callWith( () -> contentService.contentExists( child.getId() ) ) );

        this.contentService.unpublishContent( UnpublishContentParams.create().
            contentIds( ContentIds.from( content.getId() ) ).
            unpublishBranch( ContentConstants.BRANCH_MASTER ).
            build() );

        assertNotNull( contentService.contentExists( content.getId() ) );
        assertNotNull( contentService.contentExists( child.getId() ) );
        assertFalse( masterContext.callWith( () -> contentService.contentExists( content.getId() ) ) );
        assertFalse( masterContext.callWith( () -> contentService.contentExists( child.getId() ) ) );
    }

    @Test
    public void audit_data()
        throws Exception
    {
        final ArgumentCaptor<LogAuditLogParams> captor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        final Content content = this.contentService.create( CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build() );

        this.contentService.publish( PublishContentParams.create().
            target( ContentConstants.BRANCH_MASTER ).
            contentIds( ContentIds.from( content.getId() ) ).
            build() );

        final Context masterContext = ContextBuilder.from( ContextAccessor.current() ).
            branch( ContentConstants.BRANCH_MASTER ).
            build();

        assertTrue( masterContext.callWith( () -> contentService.contentExists( content.getId() ) ) );

        this.contentService.unpublishContent( UnpublishContentParams.create().
            contentIds( ContentIds.from( content.getId() ) ).
            unpublishBranch( ContentConstants.BRANCH_MASTER ).
            build() );

        Mockito.verify( auditLogService, Mockito.timeout( 5000 ).times( 3 ) ).log( captor.capture() );

        final PropertySet logResultSet = captor.getValue().getData().getSet( "result" );

        assertEquals( content.getId().toString(), logResultSet.getStrings( "unpublishedContents" ).iterator().next() );
    }

}
