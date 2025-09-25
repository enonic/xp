package com.enonic.xp.core.content;

import java.time.Instant;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.WorkflowCheckState;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.content.WorkflowState;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.verify;

public class ContentServiceImplTest_create
    extends AbstractContentServiceTest
{

    @Test
    public void create_content_generated_properties()
        throws Exception
    {
        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
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
    public void create_with_root_language()
        throws Exception
    {
        final Content root = this.contentService.getByPath( ContentPath.ROOT );
        contentService.update( new UpdateContentParams().
            contentId( root.getId() ).
            editor( edit -> edit.language = Locale.ENGLISH ) );

        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            build();

        final Content content = this.contentService.create( createContentParams );

        final Content storedContent = this.contentService.getById( content.getId() );

        assertEquals( Locale.ENGLISH, storedContent.getLanguage() );
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
                checks( Map.of( "My check", WorkflowCheckState.REJECTED ) ).
                build() ).
            build();

        final Content content = this.contentService.create( createContentParams );
        assertNotNull( content.getWorkflowInfo() );
        assertEquals( WorkflowState.PENDING_APPROVAL, content.getWorkflowInfo().getState() );
        assertEquals( Map.of( "My check", WorkflowCheckState.REJECTED ), content.getWorkflowInfo().getChecks() );

        final Content storedContent = this.contentService.getById( content.getId() );
        assertNotNull( storedContent.getWorkflowInfo() );
        assertEquals( WorkflowState.PENDING_APPROVAL, storedContent.getWorkflowInfo().getState() );
        assertEquals( Map.of( "My check", WorkflowCheckState.REJECTED ), storedContent.getWorkflowInfo().getChecks() );
    }

    @Test
    public void create_with_page()
        throws Exception
    {
        final PropertyTree config = new PropertyTree();
        config.addString( "some", "line" );

        final Form pageDescriptorForm = Form.create()
            .addFormItem( Input.create().inputType( InputTypeName.TEXT_LINE ).name( "some" ).label( "label" ).build() )
            .build();

        final DescriptorKey pageDescriptorKey = DescriptorKey.from( "abc:abc" );

        Mockito.when( pageDescriptorService.getByKey( pageDescriptorKey ) )
            .thenReturn( PageDescriptor.create()
                             .displayName( "Landing page" )
                             .config( pageDescriptorForm )
                             .regions( RegionDescriptors.create().build() )
                             .key( DescriptorKey.from( "module:landing-page" ) )
                             .build() );

        final Page page = Page.create().descriptor( pageDescriptorKey ).config( config ).regions( PageRegions.create().build() ).build();

        final PropertyTree contentData = new PropertyTree();
        contentData.addString( "title", "This is my page" );

        final CreateContentParams createContentParams = CreateContentParams.create()
            .contentData( contentData )
            .displayName( "This is my page" )
            .parent( ContentPath.ROOT )
            .type( ContentTypeName.site() )
            .page( page )
            .build();

        final Content content = this.contentService.create( createContentParams );
        assertNotNull( content.getPage() );
        assertEquals( "abc:abc", content.getPage().getDescriptor().toString() );
        assertEquals( "line", content.getPage().getConfig().getString( "some" ) );
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

        Mockito.reset( auditLogService );

        final Content content = this.contentService.create( createContentParams );

        verify( auditLogService, atMostOnce() ).log( captor.capture() );

        final LogAuditLogParams log = captor.getValue();
        assertThat( log ).extracting( LogAuditLogParams::getType).isEqualTo( "system.content.create" ) ;
        assertThat( log ).extracting( l -> l.getData().getSet( "result" ) )
            .extracting( result -> result.getString( "id" ), result -> result.getString( "path" ) )
            .containsExactly( content.getId().toString(), content.getPath().toString() );
    }
}
