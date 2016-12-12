package com.enonic.xp.repo.impl.repository;

import org.junit.Test;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.repo.impl.node.AbstractNodeTest;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static org.junit.Assert.*;

public class RepositoryServiceImplTest
    extends AbstractNodeTest
{

    public static final User REPO_TEST_DEFAULT_USER =
        User.create().key( PrincipalKey.ofUser( UserStoreKey.system(), "repo-test-user" ) ).login( "repo-test-user" ).build();

    public static final AuthenticationInfo REPO_TEST_DEFAULT_USER_AUTHINFO = AuthenticationInfo.create().
        principals( RoleKeys.AUTHENTICATED ).
        principals( RoleKeys.ADMIN ).
        user( REPO_TEST_DEFAULT_USER ).
        build();

    private final static Context ADMIN_CONTEXT = ContextBuilder.create().
        branch( "master" ).
        repositoryId( SystemConstants.SYSTEM_REPO.getId() ).
        authInfo( REPO_TEST_DEFAULT_USER_AUTHINFO ).
        build();

    @Test
    public void create()
        throws Exception
    {
        final Repository repo = doCreateRepo( "fisk" );
        assertNotNull( repo );
        assertEquals( RepositoryId.from( "fisk" ), repo.getId() );
    }

    @Test
    public void get()
        throws Exception
    {
        final Repository repo = doCreateRepo( "fisk" );

        final Repository persistedRepo = ADMIN_CONTEXT.callWith( () -> this.repositoryService.get( repo.getId() ) );
        assertNotNull( persistedRepo );
    }

    private Repository doCreateRepo( final String id )
    {
        return ADMIN_CONTEXT.callWith( () -> this.repositoryService.createRepository( CreateRepositoryParams.create().
            repositoryId( RepositoryId.from( id ) ).
            build() ) );
    }
}