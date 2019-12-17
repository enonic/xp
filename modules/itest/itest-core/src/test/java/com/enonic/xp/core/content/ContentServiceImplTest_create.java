package com.enonic.xp.core.content;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteSource;

import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.WorkflowCheckState;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.content.WorkflowState;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.site.CreateSiteParams;
import com.enonic.xp.site.SiteConfigs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContentServiceImplTest_create
    extends AbstractContentServiceTest
{

    @Test
    public void create_content_generated_properties()
        throws Exception
    {
        final PropertySet propertySet = new PropertySet();
        propertySet.setString( "nested_prop", "value" );

        final PropertyTree data = new PropertyTree();
        data.addSet( "prop",  propertySet );


        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( data ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build();

        final Content content = this.contentService.create( createContentParams );

        assertNotNull( content.getName() );
        assertEquals( "this-is-my-content", content.getName().toString() );
        assertNotNull( content.getCreatedTime() );
        assertNotNull( content.getCreator() );
        assertNotNull( content.getModifiedTime() );
        assertNotNull( content.getModifier() );
        assertNotNull( content.getChildOrder() );
        assertEquals( ContentConstants.DEFAULT_CHILD_ORDER, content.getChildOrder() );

        final Content storedContent = this.contentService.getById( content.getId() );

        assertNotNull( storedContent.getName() );
        assertEquals( "this-is-my-content", storedContent.getName().toString() );
        assertNotNull( storedContent.getCreatedTime() );
        assertNotNull( storedContent.getCreator() );
        assertNotNull( storedContent.getModifiedTime() );
        assertNotNull( storedContent.getModifier() );
        assertNotNull( storedContent.getChildOrder() );
        assertEquals( ContentConstants.DEFAULT_CHILD_ORDER, storedContent.getChildOrder() );
        assertEquals( "value", storedContent.getData().getSet( "prop" ).getString( "nested_prop" ) );
    }

    @Test
    public void create_content_unnamed()
        throws Exception
    {
        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build();

        final Content content = this.contentService.create( createContentParams );

        final Content storedContent = this.contentService.getById( content.getId() );

        assertNotNull( storedContent.getName() );
        assertTrue( storedContent.getName().isUnnamed() );
        assertTrue( storedContent.getName().hasUniqueness() );
        assertNotNull( storedContent.getCreatedTime() );
        assertNotNull( storedContent.getCreator() );
        assertNotNull( storedContent.getModifiedTime() );
        assertNotNull( storedContent.getModifier() );
        assertNotNull( storedContent.getChildOrder() );
        assertEquals( ContentConstants.DEFAULT_CHILD_ORDER, storedContent.getChildOrder() );
    }

    @Test
    public void create_with_attachments()
        throws Exception
    {
        final String name = "cat-small.jpg";
        final ByteSource image = loadImage( name );

        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.imageMedia() ).
            createAttachments( createAttachment( "cat", "image/jpeg", image ) ).
            build();

        final Content content = this.contentService.create( createContentParams );

        final Content storedContent = this.contentService.getById( content.getId() );

        final Attachments attachments = storedContent.getAttachments();
        assertEquals( 1, attachments.getSize() );
    }

    @Test
    public void create_site()
        throws Exception
    {
        final CreateSiteParams createSiteParams = new CreateSiteParams();
        createSiteParams.parent( ContentPath.ROOT ).
            displayName( "My site" ).
            description( "This is my site" ).
            siteConfigs( SiteConfigs.empty() );

        final Content content = this.contentService.create( createSiteParams );

        assertNotNull( content.getName() );
        assertNotNull( content.getCreatedTime() );
        assertNotNull( content.getCreator() );
        assertNotNull( content.getModifiedTime() );
        assertNotNull( content.getModifier() );
    }

    @Test
    public void create_incorrect_content()
        throws Exception
    {
        final PropertyTree contentData = new PropertyTree();
        contentData.addString( "target", "aStringValue" );

        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( contentData ).
            displayName( "This is my shortcut" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.shortcut() ).
            build();

        assertThrows( IllegalArgumentException.class, () -> {
            this.contentService.create( createContentParams );
        } );
    }

    @Test
    public void create_with_publish_info()
        throws Exception
    {
        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            contentPublishInfo( ContentPublishInfo.create().
                from( Instant.parse( "2016-11-03T10:42:00Z" ) ).
                from( Instant.parse( "2016-11-23T10:42:00Z" ) ).
                build() ).
            build();

        final Content content = this.contentService.create( createContentParams );
        assertNotNull( content.getPublishInfo() );
        assertNotNull( content.getPublishInfo().getFrom() );

        final Content storedContent = this.contentService.getById( content.getId() );
        assertNotNull( storedContent.getPublishInfo() );
        assertNotNull( storedContent.getPublishInfo().getFrom() );
    }

    @Test
    public void create_with_workflow_info()
        throws Exception
    {
        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            workflowInfo( WorkflowInfo.create().
                state( WorkflowState.PENDING_APPROVAL ).
                checks( ImmutableMap.of( "My check", WorkflowCheckState.REJECTED ) ).
                build() ).
            build();

        final Content content = this.contentService.create( createContentParams );
        assertNotNull( content.getWorkflowInfo() );
        assertEquals( WorkflowState.PENDING_APPROVAL, content.getWorkflowInfo().getState() );
        assertEquals( ImmutableMap.of( "My check", WorkflowCheckState.REJECTED ), content.getWorkflowInfo().getChecks() );

        final Content storedContent = this.contentService.getById( content.getId() );
        assertNotNull( storedContent.getWorkflowInfo() );
        assertEquals( WorkflowState.PENDING_APPROVAL, storedContent.getWorkflowInfo().getState() );
        assertEquals( ImmutableMap.of( "My check", WorkflowCheckState.REJECTED ), storedContent.getWorkflowInfo().getChecks() );
    }

    @Test
    public void audit_data()
    {
        final ArgumentCaptor<LogAuditLogParams> captor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build();

        final Content content = this.contentService.create( createContentParams );

        Mockito.verify( auditLogService, Mockito.timeout( 5000 ).times( 1 ) ).log( captor.capture() );

        final PropertySet logResultSet = captor.getValue().getData().getSet( "result" );

        assertEquals( content.getId().toString(), logResultSet.getString( "id" ) );
        assertEquals( content.getPath().toString(), logResultSet.getString( "path" ) );
    }
}
