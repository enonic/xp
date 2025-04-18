package com.enonic.xp.core.impl.content;

import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.audit.AuditLogService;
import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
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
