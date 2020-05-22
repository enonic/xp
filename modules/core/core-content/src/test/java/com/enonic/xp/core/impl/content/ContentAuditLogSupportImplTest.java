package com.enonic.xp.core.impl.content;

import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
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

public class ContentAuditLogSupportImplTest
{

    @Test
    public void testCreateContent()
        throws Exception
    {
        // mock
        final ContentConfig config = Mockito.mock( ContentConfig.class );

        final AuditLogService auditLogService = Mockito.mock( AuditLogService.class );

        Mockito.when( config.auditlog_enabled() ).thenReturn( true );

        // prepare
        final ExecutorService executor = Executors.newSingleThreadExecutor();

        final ContentAuditLogSupportImpl support = new ContentAuditLogSupportImpl( config, executor, auditLogService );

        final PropertyTree propertyTree = new PropertyTree();
        propertyTree.addString( "test-data", "test-data" );

        final CreateContentParams params = CreateContentParams.create().
            type( ContentTypeName.site() ).
            parent( ContentPath.ROOT ).
            contentData( propertyTree ).
            displayName( "displayName" ).build();

        final Content content = Content.create().
            id( ContentId.from( "contentId" ) ).
            type( ContentTypeName.site() ).
            name( "contentName" ).
            displayName( "displayName" ).
            parentPath( ContentPath.ROOT ).
            build();

        final User user = User.create().
            key( PrincipalKey.ofUser( IdProviderKey.system(), "testUser" ) ).
            displayName( "Test User" ).
            modifiedTime( Instant.now() ).
            email( "test-user@enonic.com" ).
            login( "test-user" ).
            build();

        final AuthenticationInfo authInfo = AuthenticationInfo.create().
            user( user ).
            principals( RoleKeys.ADMIN_LOGIN ).
            build();

        final Context context = ContextBuilder.create().
            branch( ContentConstants.BRANCH_DRAFT ).
            repositoryId( RepositoryId.from( "test-repository" ) ).
            authInfo( authInfo ).
            build();

        // test
        context.runWith( () -> support.createContent( params, content ) );

        executor.shutdown();
        executor.awaitTermination( 1, TimeUnit.MINUTES );

        // verify and assert
        final ArgumentCaptor<LogAuditLogParams> argumentCaptor = ArgumentCaptor.forClass( LogAuditLogParams.class );

        Mockito.verify( auditLogService, Mockito.times( 1 ) ).log( argumentCaptor.capture() );

        Assertions.assertEquals( user.getKey(), argumentCaptor.getValue().getUser() );
    }


}
