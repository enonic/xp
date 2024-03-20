package com.enonic.xp.core.impl.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.processor.ProcessUpdateParams;
import com.enonic.xp.content.processor.ProcessUpdateResult;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProjectAccessSiteProcessorTest
{
    private static final User TEST_USER = User.create().
        key( PrincipalKey.from( "user:system:test-user" ) ).
        displayName( "Test User" ).
        login( "test-user" ).
        build();

    private final ProjectAccessSiteProcessor projectAccessSiteProcessor = new ProjectAccessSiteProcessor();

    private ProjectPermissionsContextManagerImpl projectPermissionsContextManager;

    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.projectPermissionsContextManager = new ProjectPermissionsContextManagerImpl();
        this.projectAccessSiteProcessor.setProjectPermissionsContextManager( this.projectPermissionsContextManager );
    }

    @Test
    public void testSupports()
    {
        ContentType contentType = ContentType.create().superType( ContentTypeName.structured() ).name( ContentTypeName.site() ).build();
        assertTrue( projectAccessSiteProcessor.supports( contentType ) );

        contentType = ContentType.create().superType( ContentTypeName.structured() ).name( ContentTypeName.media() ).build();
        assertFalse( projectAccessSiteProcessor.supports( contentType ) );
    }

    @Test
    public void testProcessUpdateByAdmin()
    {
        final Context context = ContextBuilder.from( ContextAccessor.current() )
            .repositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) )
            .authInfo( AuthenticationInfo.create().user( TEST_USER ).principals( RoleKeys.ADMIN ).build() )
            .build();

        context.runWith( () -> {
            final ProcessUpdateParams params = createProcessUpdateParams( "white", "blue" );
            final ProcessUpdateResult result = this.projectAccessSiteProcessor.processUpdate( params );

            assertNull( result );

        } );
    }

    @Test
    public void testProcessUpdateByContentAdmin()
    {
        final Context context = ContextBuilder.from( ContextAccessor.current() )
            .repositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) )
            .authInfo( AuthenticationInfo.create().user( TEST_USER ).principals( RoleKeys.CONTENT_MANAGER_ADMIN ).build() )
            .build();

        context.runWith( () -> {
            final ProcessUpdateParams params = createProcessUpdateParams( "white", "blue" );
            final ProcessUpdateResult result = this.projectAccessSiteProcessor.processUpdate( params );

            assertNull( result );
        } );
    }

    @Test
    public void testProcessUpdateWithNoChanges()
    {
        final Context context = ContextBuilder.from( ContextAccessor.current() )
            .repositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) )
            .authInfo( AuthenticationInfo.create().user( TEST_USER ).principals( RoleKeys.ADMIN ).build() )
            .build();

        context.runWith( () -> {
            final ProcessUpdateParams params = createProcessUpdateParams( "white", "white" );
            final ProcessUpdateResult result = this.projectAccessSiteProcessor.processUpdate( params );

            assertNull( result );
        } );
    }

    @Test
    public void testProcessUpdateWithNoRights()
        throws ProjectAccessRequiredException
    {
        final Context context = ContextBuilder.from( ContextAccessor.current() )
            .repositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) )
            .authInfo( AuthenticationInfo.create().user( TEST_USER ).build() )
            .build();

        context.runWith( () -> {
            final ProcessUpdateParams params = createProcessUpdateParams( "white", "blue" );

            assertThrows( ProjectAccessRequiredException.class, () -> this.projectAccessSiteProcessor.processUpdate( params ) );
        } );
    }

    @Test
    public void testProcessUpdateWithNoRightsAndNoChanges()
    {
        final Context context = ContextBuilder.from( ContextAccessor.current() )
            .repositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) )
            .authInfo( AuthenticationInfo.create().user( TEST_USER ).build() )
            .build();

        context.runWith( () -> {
            final ProcessUpdateParams params = createProcessUpdateParams( "white", "white" );
            final ProcessUpdateResult result = this.projectAccessSiteProcessor.processUpdate( params );

            assertNull( result );
        } );
    }

    private ProcessUpdateParams createProcessUpdateParams( String oldValue, String newValue )
    {
        final Content originalContent = this.createContent( oldValue );
        final Content editedContent = this.createContent( newValue );

        return ProcessUpdateParams.create().
            originalContent( originalContent ).
            editedContent( editedContent ).
            contentType( this.createSiteContentType() ).
            build();
    }

    private ContentType createSiteContentType()
    {
        return ContentType.create().name( ContentTypeName.site() ).superType( ContentTypeName.structured() ).build();
    }

    private Content createContent( final String siteConfigBgColor )
    {
        PropertyTree data = new PropertyTree();
        PropertySet set = data.newSet();
        set.addString( "applicationKey", "com.enonic.app.test" );
        PropertySet siteConfig = set.addSet( "config" );
        siteConfig.addString( "backgroundColor", siteConfigBgColor );
        data.setSet( "siteConfig", set );
        return Content.create( ContentTypeName.site() ).name( "Site content" ).parentPath( ContentPath.ROOT ).data( data ).id(
            ContentId.from( "content-id" ) ).build();
    }
}
