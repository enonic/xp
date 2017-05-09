package com.enonic.xp.repo.impl.node;

import java.util.concurrent.Callable;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.MultiRepoNodeQuery;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.SearchTarget;
import com.enonic.xp.node.SearchTargets;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static com.enonic.xp.repository.RepositoryConstants.MASTER_BRANCH;
import static org.junit.Assert.*;

public class FindNodesByMultiRepoQueryCommandTest
    extends AbstractNodeTest
{
    private static final User REPO_USER_1 =
        User.create().key( PrincipalKey.ofUser( UserStoreKey.system(), "repo-user-1" ) ).login( "repo-user-1" ).build();

    private static final User REPO_USER_2 =
        User.create().key( PrincipalKey.ofUser( UserStoreKey.system(), "repo-user-2" ) ).login( "repo-user-2" ).build();

    private static final User REPO_USER_3 =
        User.create().key( PrincipalKey.ofUser( UserStoreKey.system(), "repo-user-3" ) ).login( "repo-user-3" ).build();

    @Override
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.createDefaultRootNode();
    }

    @Test
    public void multi_repo_search()
        throws Exception
    {
        final Repository repo1 = createRepo( REPO_USER_1, "repo1" );
        final Repository repo2 = createRepo( REPO_USER_2, "repo2" );

        runInContext( REPO_USER_1, repo1.getId(), () -> createNode( NodePath.ROOT, "repo1Node" ) );
        runInContext( REPO_USER_2, repo2.getId(), () -> createNode( NodePath.ROOT, "repo2Node" ) );

        final SearchTargets targets = SearchTargets.create().
            add( createTarget( MASTER_BRANCH, REPO_USER_1, repo1.getId() ) ).
            add( createTarget( MASTER_BRANCH, REPO_USER_2, repo2.getId() ) ).
            build();

        final NodeQuery query = NodeQuery.create().
            parent( NodePath.ROOT ).
            build();

        final FindNodesByQueryResult result = doQuery( query, targets );

        assertEquals( 2L, result.getTotalHits() );
    }

    @Test
    public void no_access_in_one_repo()
        throws Exception
    {
        final Repository repo1 = createRepo( REPO_USER_1, "repo1" );
        final Repository repo2 = createRepo( REPO_USER_2, "repo2" );

        runInContext( REPO_USER_1, repo1.getId(), () -> createNode( NodePath.ROOT, "repo1Node" ) );
        runInContext( REPO_USER_2, repo2.getId(), () -> createNode( NodePath.ROOT, "repo2Node" ) );

        final SearchTargets targets = SearchTargets.create().
            add( createTarget( MASTER_BRANCH, REPO_USER_1, repo1.getId() ) ).
            add( createTarget( MASTER_BRANCH, REPO_USER_1, repo2.getId() ) ). // repoUser2 has no access
            build();

        final NodeQuery query = NodeQuery.create().
            parent( NodePath.ROOT ).
            build();

        final FindNodesByQueryResult result = doQuery( query, targets );

        assertEquals( 1L, result.getTotalHits() );
    }

    @Test
    public void no_access_in_one_repo_of_three()
        throws Exception
    {
        final Repository repo1 = createRepo( REPO_USER_1, "repo1" );
        final Repository repo2 = createRepo( REPO_USER_2, "repo2" );
        final Repository repo3 = createRepo( REPO_USER_3, "repo3" );

        runInContext( REPO_USER_1, repo1.getId(), () -> createNode( NodePath.ROOT, "repo1Node" ) );
        runInContext( REPO_USER_2, repo2.getId(), () -> createNode( NodePath.ROOT, "repo2Node" ) );
        runInContext( REPO_USER_3, repo3.getId(), () -> createNode( NodePath.ROOT, "repo2Node" ) );

        final SearchTargets targets = SearchTargets.create().
            add( createTarget( MASTER_BRANCH, REPO_USER_1, repo1.getId() ) ).
            add( createTarget( MASTER_BRANCH, REPO_USER_2, repo2.getId() ) ).
            add( createTarget( MASTER_BRANCH, REPO_USER_1, repo3.getId() ) ). // repoUser1 has no access
            build();

        final NodeQuery query = NodeQuery.create().
            parent( NodePath.ROOT ).
            build();

        final FindNodesByQueryResult result = doQuery( query, targets );

        assertEquals( 2L, result.getTotalHits() );
    }

    private FindNodesByQueryResult doQuery( final NodeQuery query, final SearchTargets targets )
    {
        return FindNodesByMultiRepoQueryCommand.create().
            query( new MultiRepoNodeQuery( targets, query ) ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();
    }

    private SearchTarget createTarget( final Branch branch, final User user, final RepositoryId repoId )
    {
        return SearchTarget.create().
            repositoryId( repoId ).
            branch( branch ).
            principalKeys( PrincipalKeys.from( user.getKey() ) ).
            build();
    }

    private void runInContext( final User runUser, final RepositoryId repoId, final Runnable runnable )
    {
        ContextBuilder.create().
            repositoryId( repoId ).
            branch( MASTER_BRANCH ).
            authInfo( AuthenticationInfo.create().user( runUser ).build() ).
            build().runWith( runnable );
    }

    private Repository createRepo( final User adminUser, final String repoName )
    {
        return NodeHelper.runAsAdmin( () -> this.repositoryService.createRepository( CreateRepositoryParams.create().
            repositoryId( RepositoryId.from( repoName ) ).
            rootPermissions( AccessControlList.create().
                add( AccessControlEntry.create().
                    principal( adminUser.getKey() ).
                    allowAll().
                    build() ).
                build() ).
            build() ) );
    }
}