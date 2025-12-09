package com.enonic.xp.core.impl.content;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.archive.ArchiveContentParams;
import com.enonic.xp.archive.ArchiveContentsResult;
import com.enonic.xp.archive.RestoreContentParams;
import com.enonic.xp.archive.RestoreContentsResult;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ApplyContentPermissionsParams;
import com.enonic.xp.content.ApplyContentPermissionsResult;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.CreateMediaParams;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.content.DeleteContentsResult;
import com.enonic.xp.content.DuplicateContentParams;
import com.enonic.xp.content.DuplicateContentsResult;
import com.enonic.xp.content.Media;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.content.MoveContentsResult;
import com.enonic.xp.content.PatchContentParams;
import com.enonic.xp.content.PatchContentResult;
import com.enonic.xp.content.ProjectSyncParams;
import com.enonic.xp.content.PublishContentResult;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.content.ResetContentInheritParams;
import com.enonic.xp.content.SortContentParams;
import com.enonic.xp.content.SortContentResult;
import com.enonic.xp.content.UnpublishContentParams;
import com.enonic.xp.content.UnpublishContentsResult;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.content.UpdateMediaParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ContentAuditLogSupportImplTest
{

    private ExecutorService executor;

    private ContentAuditLogSupportImpl support;

    private AuditLogService auditLogService;

    @BeforeEach
    void setUp()
    {
        // mock
        final ContentConfig config = Mockito.mock( ContentConfig.class );

        auditLogService = Mockito.mock( AuditLogService.class );
        ContentAuditLogFilterService contentAuditLogFilterService = Mockito.mock( ContentAuditLogFilterService.class, invocation -> true );

        Mockito.when( config.auditlog_enabled() ).thenReturn( true );

        executor = Executors.newSingleThreadExecutor();

        support = new ContentAuditLogSupportImpl( config, executor, auditLogService, contentAuditLogFilterService );
    }

    @Test
    void testCreateContent()
        throws Exception
    {
        final PropertyTree propertyTree = createTestPropertyTree();

        final CreateContentParams params = CreateContentParams.create()
            .type( ContentTypeName.site() )
            .parent( ContentPath.ROOT )
            .contentData( propertyTree )
            .displayName( "displayName" )
            .build();

        final Content content = Content.create()
            .id( ContentId.from( "contentId" ) )
            .type( ContentTypeName.site() )
            .name( "contentName" )
            .displayName( "displayName" )
            .parentPath( ContentPath.ROOT )
            .build();

        test( support::createContent, params, content );
    }

    @Test
    void testUpdateContent()
        throws Exception
    {
        final UpdateContentParams params = new UpdateContentParams().requireValid( true )
            .contentId( ContentId.from( "contentId" ) )
            .clearAttachments( true )
            .editor( edit -> edit.displayName = "New Display Name" );

        final Content content = Content.create()
            .id( ContentId.from( "contentId" ) )
            .type( ContentTypeName.site() )
            .name( "contentName" )
            .displayName( "displayName" )
            .parentPath( ContentPath.ROOT )
            .build();

        test( support::update, params, content );
    }

    @Test
    void testPatchContent()
        throws Exception
    {
        final PatchContentParams params = PatchContentParams.create()
            .contentId( ContentId.from( "contentId" ) )
            .branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) )
            .skipSync( true )
            .createAttachments( CreateAttachments.from( CreateAttachment.create()
                                                            .mimeType( "image/jpeg" )
                                                            .label( "My Image 1" )
                                                            .name( "MyImage.jpg" )
                                                            .byteSource( ByteSource.wrap( "data".getBytes( StandardCharsets.UTF_8 ) ) )
                                                            .build(), CreateAttachment.create()
                                                            .mimeType( "plain/text" )
                                                            .label( "My Text 1" )
                                                            .name( "MyText.txp" )
                                                            .byteSource( ByteSource.wrap( "text data".getBytes( StandardCharsets.UTF_8 ) ) )
                                                            .text( "text data" )
                                                            .build() ) ).patcher( edit -> edit.displayName.setValue( "New Display Name" ) )
            .build();

        final PatchContentResult result = PatchContentResult.create()
            .contentId( ContentId.from( "contentId" ) )
            .addResult( ContentConstants.BRANCH_DRAFT, Content.create()
                .id( ContentId.from( "contentId" ) )
                .type( ContentTypeName.site() )
                .name( "contentName1" )
                .displayName( "displayName1" )
                .parentPath( ContentPath.ROOT )
                .build() )
            .addResult( ContentConstants.BRANCH_MASTER, Content.create()
                .id( ContentId.from( "contentId" ) )
                .type( ContentTypeName.site() )
                .name( "contentName2" )
                .displayName( "displayName2" )
                .parentPath( ContentPath.ROOT )
                .build() )
            .build();

        //test
        ArgumentCaptor<LogAuditLogParams> argumentCaptor = test( support::patch, params, result );

        assertEquals( "user:system:testUser", argumentCaptor.getValue().getData().getSet( "params" ).getString( "modifier" ) );
        assertEquals( "contentId", argumentCaptor.getValue().getData().getSet( "params" ).getString( "contentId" ) );
        assertEquals( "true", argumentCaptor.getValue().getData().getSet( "params" ).getString( "skipSync" ) );
        assertEquals( List.of( "draft", "master" ), argumentCaptor.getValue().getData().getSet( "params" ).getStrings( "branches" ) );
        assertEquals( "MyImage.jpg",
                      argumentCaptor.getValue().getData().getSet( "params" ).getSet( "createAttachments" ).getString( "name" ) );
        assertEquals( "image/jpeg",
                      argumentCaptor.getValue().getData().getSet( "params" ).getSet( "createAttachments" ).getString( "mimeType" ) );
        assertEquals( "My Image 1",
                      argumentCaptor.getValue().getData().getSet( "params" ).getSet( "createAttachments" ).getString( "label" ) );
        assertEquals( 4L, argumentCaptor.getValue().getData().getSet( "params" ).getSet( "createAttachments" ).getLong( "byteSize" ) );

        assertEquals( 9L, argumentCaptor.getValue().getData().getSet( "params" ).getSet( "createAttachments", 1 ).getLong( "textSize" ) );

        assertEquals( "/contentName1", argumentCaptor.getValue().getData().getSet( "result" ).getSet( "draft" ).getString( "path" ) );
        assertEquals( "/contentName2", argumentCaptor.getValue().getData().getSet( "result" ).getSet( "master" ).getString( "path" ) );
    }

    @Test
    void testResetInheritance()
        throws Exception
    {
        final ResetContentInheritParams params = ResetContentInheritParams.create()
            .contentId( ContentId.from( "contentId" ) )
            .projectName( ProjectName.from( "test-project" ) )
            .inherit( EnumSet.of( ContentInheritType.CONTENT, ContentInheritType.SORT ) )
            .build();

        ArgumentCaptor<LogAuditLogParams> argumentCaptor = testSingle( support::resetInheritance, params );

        assertEquals( "system.contentSync.resetInheritance", argumentCaptor.getValue().getType() );
        assertEquals( "contentId", argumentCaptor.getValue().getData().getSet( "params" ).getString( "contentId" ) );
        assertEquals( "test-project", argumentCaptor.getValue().getData().getSet( "params" ).getString( "projectName" ) );
        assertEquals( List.of( "CONTENT", "SORT" ), argumentCaptor.getValue().getData().getSet( "params" ).getStrings( "inherit" ) );
    }

    @Test
    void testSyncProject()
        throws Exception
    {
        final ProjectSyncParams params = ProjectSyncParams.create()
            .targetProject( ProjectName.from( "target-project" ) )
            .build();

        ArgumentCaptor<LogAuditLogParams> argumentCaptor = testSingle( support::syncProject, params );

        assertEquals( "system.contentSync.syncProject", argumentCaptor.getValue().getType() );
        assertEquals( "target-project", argumentCaptor.getValue().getData().getSet( "params" ).getString( "targetProject" ) );
    }

    @Test
    void testCreateMedia()
        throws Exception
    {
        final CreateMediaParams params = new CreateMediaParams();
        params.name( "mediaName" );
        params.parent( ContentPath.ROOT );
        params.mimeType( "image/jpeg" );

        final Content content = Media.create()
            .id( ContentId.from( "mediaId" ) )
            .type( ContentTypeName.imageMedia() )
            .name( "mediaName" )
            .displayName( "Media Display Name" )
            .parentPath( ContentPath.ROOT )
            .build();

        ArgumentCaptor<LogAuditLogParams> argumentCaptor = test( support::createMedia, params, content );

        assertEquals( "system.content.create", argumentCaptor.getValue().getType() );
        assertEquals( "mediaName", argumentCaptor.getValue().getData().getSet( "params" ).getString( "name" ) );
        assertEquals( "image/jpeg", argumentCaptor.getValue().getData().getSet( "params" ).getString( "mimeType" ) );
        assertEquals( "/", argumentCaptor.getValue().getData().getSet( "params" ).getString( "parent" ) );
        assertEquals( "mediaId", argumentCaptor.getValue().getData().getSet( "result" ).getString( "id" ) );
        assertEquals( "/mediaName", argumentCaptor.getValue().getData().getSet( "result" ).getString( "path" ) );
    }

    @Test
    void testUpdateMedia()
        throws Exception
    {
        final UpdateMediaParams params = new UpdateMediaParams();
        params.content( ContentId.from( "mediaId" ) );
        params.name( "updatedMediaName" );
        params.mimeType( "image/png" );

        final Content content = Media.create()
            .id( ContentId.from( "mediaId" ) )
            .type( ContentTypeName.imageMedia() )
            .name( "updatedMediaName" )
            .displayName( "Updated Media" )
            .parentPath( ContentPath.ROOT )
            .build();

        ArgumentCaptor<LogAuditLogParams> argumentCaptor = test( support::update, params, content );

        assertEquals( "system.content.update", argumentCaptor.getValue().getType() );
        assertEquals( "updatedMediaName", argumentCaptor.getValue().getData().getSet( "params" ).getString( "name" ) );
        assertEquals( "image/png", argumentCaptor.getValue().getData().getSet( "params" ).getString( "mimeType" ) );
        assertEquals( "mediaId", argumentCaptor.getValue().getData().getSet( "params" ).getString( "content" ) );
        assertEquals( "mediaId", argumentCaptor.getValue().getData().getSet( "result" ).getString( "id" ) );
        assertEquals( "/updatedMediaName", argumentCaptor.getValue().getData().getSet( "result" ).getString( "path" ) );
    }

    @Test
    void testDelete()
        throws Exception
    {
        final DeleteContentParams params = DeleteContentParams.create()
            .contentPath( ContentPath.from( "/content-to-delete" ) )
            .build();

        final DeleteContentsResult result = DeleteContentsResult.create()
            .addDeleted( ContentId.from( "deletedId" ) )
            .addUnpublished( ContentId.from( "unpublishedId" ) )
            .build();

        ArgumentCaptor<LogAuditLogParams> argumentCaptor = test( support::delete, params, result );

        assertEquals( "system.content.delete", argumentCaptor.getValue().getType() );
        assertEquals( "/content-to-delete", argumentCaptor.getValue().getData().getSet( "params" ).getString( "contentPath" ) );
        assertEquals( List.of( "deletedId" ), argumentCaptor.getValue().getData().getSet( "result" ).getStrings( "deletedContents" ) );
        assertEquals( List.of( "unpublishedId" ), argumentCaptor.getValue().getData().getSet( "result" ).getStrings( "unpublishedContents" ) );
    }

    @Test
    void testPublish()
        throws Exception
    {
        final PushContentParams params = PushContentParams.create()
            .contentIds( ContentIds.from( ContentId.from( "contentId" ) ) )
            .build();

        final PublishContentResult result = PublishContentResult.create()
            .add( PublishContentResult.Result.success( ContentId.from( "contentId" ) ) )
            .build();

        ArgumentCaptor<LogAuditLogParams> argumentCaptor = test( support::publish, params, result );

        assertEquals( "system.content.publish", argumentCaptor.getValue().getType() );
        assertEquals( List.of( "contentId" ), argumentCaptor.getValue().getData().getSet( "params" ).getStrings( "contentIds" ) );
        assertEquals( List.of( "contentId" ), argumentCaptor.getValue().getData().getSet( "result" ).getStrings( "pushedContents" ) );
    }

    @Test
    void testUnpublishContent()
        throws Exception
    {
        final UnpublishContentParams params = UnpublishContentParams.create()
            .contentIds( ContentIds.from( ContentId.from( "contentId" ) ) )
            .build();

        final UnpublishContentsResult result = UnpublishContentsResult.create()
            .addUnpublished( ContentId.from( "contentId" ) )
            .build();

        ArgumentCaptor<LogAuditLogParams> argumentCaptor = test( support::unpublishContent, params, result );

        assertEquals( "system.content.unpublishContent", argumentCaptor.getValue().getType() );
        assertEquals( List.of( "contentId" ), argumentCaptor.getValue().getData().getSet( "params" ).getStrings( "contentIds" ) );
        assertEquals( List.of( "contentId" ), argumentCaptor.getValue().getData().getSet( "result" ).getStrings( "unpublishedContents" ) );
    }

    @Test
    void testDuplicate()
        throws Exception
    {
        final DuplicateContentParams params = DuplicateContentParams.create()
            .contentId( ContentId.from( "contentId" ) )
            .build();

        final DuplicateContentsResult result = DuplicateContentsResult.create()
            .addDuplicated( ContentId.from( "duplicatedId" ) )
            .build();

        ArgumentCaptor<LogAuditLogParams> argumentCaptor = test( support::duplicate, params, result );

        assertEquals( "system.content.duplicate", argumentCaptor.getValue().getType() );
        assertEquals( "contentId", argumentCaptor.getValue().getData().getSet( "params" ).getString( "contentId" ) );
        assertEquals( List.of( "duplicatedId" ), argumentCaptor.getValue().getData().getSet( "result" ).getStrings( "duplicatedContents" ) );
    }

    @Test
    void testMove()
        throws Exception
    {
        final MoveContentParams params = MoveContentParams.create()
            .contentId( ContentId.from( "contentId" ) )
            .parentContentPath( ContentPath.from( "/new-parent" ) )
            .build();

        final MoveContentsResult result = MoveContentsResult.create()
            .addMoved( ContentId.from( "movedId" ) )
            .build();

        ArgumentCaptor<LogAuditLogParams> argumentCaptor = test( support::move, params, result );

        assertEquals( "system.content.move", argumentCaptor.getValue().getType() );
        assertEquals( "contentId", argumentCaptor.getValue().getData().getSet( "params" ).getString( "contentId" ) );
        assertEquals( "/new-parent", argumentCaptor.getValue().getData().getSet( "params" ).getString( "parentContentPath" ) );
        assertEquals( List.of( "movedId" ), argumentCaptor.getValue().getData().getSet( "result" ).getStrings( "movedContents" ) );
    }

    @Test
    void testArchive()
        throws Exception
    {
        final ArchiveContentParams params = ArchiveContentParams.create()
            .contentId( ContentId.from( "contentId" ) )
            .build();

        final ArchiveContentsResult result = ArchiveContentsResult.create()
            .addArchived( ContentId.from( "archivedId" ) )
            .build();

        ArgumentCaptor<LogAuditLogParams> argumentCaptor = test( support::archive, params, result );

        assertEquals( "system.content.archive", argumentCaptor.getValue().getType() );
        assertEquals( "contentId", argumentCaptor.getValue().getData().getSet( "params" ).getString( "contentId" ) );
        assertEquals( List.of( "archivedId" ), argumentCaptor.getValue().getData().getSet( "result" ).getStrings( "archivedContents" ) );
    }

    @Test
    void testRestore()
        throws Exception
    {
        final RestoreContentParams params = RestoreContentParams.create()
            .contentId( ContentId.from( "contentId" ) )
            .build();

        final RestoreContentsResult result = RestoreContentsResult.create()
            .addRestored( ContentId.from( "restoredId" ) )
            .parentPath( ContentPath.ROOT )
            .build();

        ArgumentCaptor<LogAuditLogParams> argumentCaptor = test( support::restore, params, result );

        assertEquals( "system.content.restore", argumentCaptor.getValue().getType() );
        assertEquals( "contentId", argumentCaptor.getValue().getData().getSet( "params" ).getString( "contentId" ) );
        assertEquals( "/", argumentCaptor.getValue().getData().getSet( "result" ).getString( "parentContentPath" ) );
        assertEquals( List.of( "restoredId" ), argumentCaptor.getValue().getData().getSet( "result" ).getStrings( "restoredContents" ) );
    }

    @Test
    void testSort()
        throws Exception
    {
        final SortContentParams params = SortContentParams.create()
            .contentId( ContentId.from( "contentId" ) )
            .build();

        final Content content = Content.create()
            .id( ContentId.from( "contentId" ) )
            .type( ContentTypeName.folder() )
            .name( "sortedContent" )
            .displayName( "Sorted Content" )
            .parentPath( ContentPath.ROOT )
            .build();

        final SortContentResult result = SortContentResult.create()
            .content( content )
            .movedChildren( ContentIds.empty() )
            .build();

        ArgumentCaptor<LogAuditLogParams> argumentCaptor = test( support::sort, params, result );

        assertEquals( "system.content.sort", argumentCaptor.getValue().getType() );
        assertEquals( "contentId", argumentCaptor.getValue().getData().getSet( "params" ).getString( "contentId" ) );
        assertEquals( "contentId", argumentCaptor.getValue().getData().getSet( "result" ).getString( "id" ) );
        assertEquals( "/sortedContent", argumentCaptor.getValue().getData().getSet( "result" ).getString( "path" ) );
    }

    @Test
    void testApplyPermissions()
        throws Exception
    {
        final ApplyContentPermissionsParams params = ApplyContentPermissionsParams.create()
            .contentId( ContentId.from( "contentId" ) )
            .build();

        final ApplyContentPermissionsResult result = ApplyContentPermissionsResult.create()
            .build();

        ArgumentCaptor<LogAuditLogParams> argumentCaptor = test( support::applyPermissions, params, result );

        assertEquals( "system.content.applyPermissions", argumentCaptor.getValue().getType() );
        assertEquals( "contentId", argumentCaptor.getValue().getData().getSet( "params" ).getString( "contentId" ) );
    }

    private <P, R> ArgumentCaptor<LogAuditLogParams> test( BiConsumer<P, R> log, P params, R result )
        throws Exception
    {
        final User testUser = createTestUser();
        createTestContext( testUser );

        //run
        createTestContext( testUser ).runWith( () -> log.accept( params, result ) );

        shutdownExecutor();
        return verifyAuditLog( testUser );
    }


    private <P> ArgumentCaptor<LogAuditLogParams> testSingle( Consumer<P> log, P params )
        throws Exception
    {
        final User testUser = createTestUser();
        createTestContext( testUser );

        //run
        createTestContext( testUser ).runWith( () -> log.accept( params ) );

        shutdownExecutor();
        return verifyAuditLog( testUser );
    }


    private User createTestUser()
    {
        return User.create()
            .key( PrincipalKey.ofUser( IdProviderKey.system(), "testUser" ) )
            .displayName( "Test User" )
            .modifiedTime( Instant.now() )
            .email( "test-user@enonic.com" )
            .login( "test-user" )
            .build();
    }

    private Context createTestContext( User user )
    {
        AuthenticationInfo authInfo = AuthenticationInfo.create().user( user ).principals( RoleKeys.ADMIN_LOGIN ).build();
        return ContextBuilder.create()
            .branch( ContentConstants.BRANCH_DRAFT )
            .repositoryId( RepositoryId.from( "test-repository" ) )
            .authInfo( authInfo )
            .build();
    }

    private PropertyTree createTestPropertyTree()
    {
        PropertyTree propertyTree = new PropertyTree();
        propertyTree.addString( "test-data", "test-data" );
        return propertyTree;
    }

    private void shutdownExecutor()
        throws InterruptedException
    {
        executor.shutdown();
        executor.awaitTermination( 1, TimeUnit.MINUTES );
    }

    private ArgumentCaptor<LogAuditLogParams> verifyAuditLog( User user )
    {
        ArgumentCaptor<LogAuditLogParams> argumentCaptor = ArgumentCaptor.forClass( LogAuditLogParams.class );
        Mockito.verify( auditLogService, Mockito.times( 1 ) ).log( argumentCaptor.capture() );
        assertEquals( user.getKey(), argumentCaptor.getValue().getUser() );

        return argumentCaptor;
    }
}
