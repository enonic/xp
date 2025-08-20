package com.enonic.xp.core.impl.content;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.google.common.io.ByteSource;

import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.PatchContentParams;
import com.enonic.xp.content.PatchContentResult;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContentAuditLogSupportImplTest
{

    private ExecutorService executor;

    private ContentAuditLogSupportImpl support;

    private AuditLogService auditLogService;

    @BeforeEach
    public void setUp()
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
    public void testCreateContent()
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
    public void testUpdateContent()
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
    public void testPatchContent()
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
        assertEquals( "true", argumentCaptor.getValue().getData().getSet( "params" ).getString( Constants.CONTENT_SKIP_SYNC ) );
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
