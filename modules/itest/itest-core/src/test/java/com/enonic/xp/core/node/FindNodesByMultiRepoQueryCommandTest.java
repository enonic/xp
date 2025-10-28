package com.enonic.xp.core.node;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.node.FindNodesByMultiRepoQueryResult;
import com.enonic.xp.node.MultiRepoNodeHit;
import com.enonic.xp.node.MultiRepoNodeHits;
import com.enonic.xp.node.MultiRepoNodeQuery;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.SearchTarget;
import com.enonic.xp.node.SearchTargets;
import com.enonic.xp.repo.impl.node.FindNodesByMultiRepoQueryCommand;
import com.enonic.xp.repo.impl.node.NodeHelper;
import com.enonic.xp.repository.CreateBranchParams;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static com.enonic.xp.repository.RepositoryConstants.MASTER_BRANCH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class FindNodesByMultiRepoQueryCommandTest
    extends AbstractNodeTest
{
    private static final User REPO_USER_1 =
        User.create().key( PrincipalKey.ofUser( IdProviderKey.system(), "repo-user-1" ) ).login( "repo-user-1" ).build();

    private static final User REPO_USER_2 =
        User.create().key( PrincipalKey.ofUser( IdProviderKey.system(), "repo-user-2" ) ).login( "repo-user-2" ).build();

    private static final User REPO_USER_3 =
        User.create().key( PrincipalKey.ofUser( IdProviderKey.system(), "repo-user-3" ) ).login( "repo-user-3" ).build();

    public FindNodesByMultiRepoQueryCommandTest()
    {
        super( true );
    }

    @BeforeEach
    void setUp()
    {
        this.createDefaultRootNode();
    }

    @Test
    void multi_repo_search()
    {
        final Repository repo1 = createRepo( REPO_USER_1, "repo1" );
        final Repository repo2 = createRepo( REPO_USER_2, "repo2" );

        runInContext( REPO_USER_1, repo1.getId(), () -> createNode( NodePath.ROOT, "repo1Node" ) );
        runInContext( REPO_USER_2, repo2.getId(), () -> createNode( NodePath.ROOT, "repo2Node" ) );
        refresh();

        final SearchTargets targets = SearchTargets.create().
            add( createTarget( MASTER_BRANCH, REPO_USER_1, repo1.getId() ) ).
            add( createTarget( MASTER_BRANCH, REPO_USER_2, repo2.getId() ) ).
            build();

        final NodeQuery query = NodeQuery.create().
            parent( NodePath.ROOT ).
            build();

        final FindNodesByMultiRepoQueryResult result = doQuery( query, targets );

        assertEquals( 2L, result.getTotalHits() );
        assertRepos( result, repo1.getId(), repo2.getId() );
        assertBranches( result, MASTER_BRANCH );
    }

    @Test
    void multi_repo_search_different_branches()
    {
        final Repository repo1 = createRepo( REPO_USER_1, "repo1" );
        final Repository repo2 = createRepo( REPO_USER_2, "repo2" );

        // Create new branch in repo1
        final Branch otherBranch = callInContext( REPO_USER_1, repo1.getId(), MASTER_BRANCH, () -> createBranch( "otherBranch" ) );

        final Node repo1MasterNode1 =
            callInContext( REPO_USER_1, repo1.getId(), MASTER_BRANCH, () -> createNode( NodePath.ROOT, "repo1Node" ) );
        final Node repo1OtherNode1 =
            callInContext( REPO_USER_1, repo1.getId(), otherBranch, () -> createNode( NodePath.ROOT, "repo1Node" ) );
        final Node repo2MasterNode1 =
            callInContext( REPO_USER_2, repo2.getId(), MASTER_BRANCH, () -> createNode( NodePath.ROOT, "repo2Node" ) );
        refresh();

        final SearchTargets targets = SearchTargets.create().
            add( createTarget( otherBranch, REPO_USER_1, repo1.getId() ) ).
            add( createTarget( MASTER_BRANCH, REPO_USER_2, repo2.getId() ) ).
            build();

        final NodeQuery query = NodeQuery.create().
            parent( NodePath.ROOT ).
            build();

        final FindNodesByMultiRepoQueryResult result = doQuery( query, targets );

        assertEquals( 2L, result.getTotalHits() );
        assertRepos( result, repo1.getId(), repo2.getId() );
        assertBranches( result, MASTER_BRANCH, otherBranch );
        assertNodes( result, repo1OtherNode1.id(), repo2MasterNode1.id() );
    }

    @Test
    void multi_repo_search_different_branches_same_repo()
    {
        final Repository repo1 = createRepo( REPO_USER_1, "repo1" );
        final Repository repo2 = createRepo( REPO_USER_2, "repo2" );

        // Create new branch in repo1
        final Branch otherBranch = callInContext( REPO_USER_1, repo1.getId(), MASTER_BRANCH, () -> createBranch( "otherBranch" ) );

        final Node repo1MasterNode1 =
            callInContext( REPO_USER_1, repo1.getId(), MASTER_BRANCH, () -> createNode( NodePath.ROOT, "repo1Node" ) );
        final Node repo1OtherNode1 =
            callInContext( REPO_USER_1, repo1.getId(), otherBranch, () -> createNode( NodePath.ROOT, "repo1Node" ) );
        final Node repo2MasterNode1 =
            callInContext( REPO_USER_2, repo2.getId(), MASTER_BRANCH, () -> createNode( NodePath.ROOT, "repo2Node" ) );
        refresh();

        final SearchTargets targets = SearchTargets.create().
            add( createTarget( otherBranch, REPO_USER_1, repo1.getId() ) ).
            add( createTarget( MASTER_BRANCH, REPO_USER_1, repo1.getId() ) ).
            add( createTarget( MASTER_BRANCH, REPO_USER_2, repo2.getId() ) ).
            build();

        final NodeQuery query = NodeQuery.create().
            parent( NodePath.ROOT ).
            build();

        final FindNodesByMultiRepoQueryResult result = doQuery( query, targets );

        assertEquals( 3L, result.getTotalHits() );
        assertRepos( result, repo1.getId(), repo2.getId() );
        assertBranches( result, MASTER_BRANCH, otherBranch );
        assertNodes( result, repo1OtherNode1.id(), repo1MasterNode1.id(), repo2MasterNode1.id() );
    }

    @Test
    void no_access_in_one_repo()
    {
        final Repository repo1 = createRepo( REPO_USER_1, "repo1" );
        final Repository repo2 = createRepo( REPO_USER_2, "repo2" );

        runInContext( REPO_USER_1, repo1.getId(), () -> createNode( NodePath.ROOT, "repo1Node" ) );
        runInContext( REPO_USER_2, repo2.getId(), () -> createNode( NodePath.ROOT, "repo2Node" ) );
        refresh();

        final SearchTargets targets = SearchTargets.create().
            add( createTarget( MASTER_BRANCH, REPO_USER_1, repo1.getId() ) ).
            add( createTarget( MASTER_BRANCH, REPO_USER_1, repo2.getId() ) ). // repoUser2 has no access
            build();

        final NodeQuery query = NodeQuery.create().
            parent( NodePath.ROOT ).
            build();

        final FindNodesByMultiRepoQueryResult result = doQuery( query, targets );

        assertEquals( 1L, result.getTotalHits() );
        assertRepos( result, repo1.getId() );
        assertBranches( result, MASTER_BRANCH );
    }

    @Test
    void no_access_in_one_repo_of_three()
    {
        final Repository repo1 = createRepo( REPO_USER_1, "repo1" );
        final Repository repo2 = createRepo( REPO_USER_2, "repo2" );
        final Repository repo3 = createRepo( REPO_USER_3, "repo3" );

        runInContext( REPO_USER_1, repo1.getId(), () -> createNode( NodePath.ROOT, "repo1Node" ) );
        runInContext( REPO_USER_2, repo2.getId(), () -> createNode( NodePath.ROOT, "repo2Node" ) );
        runInContext( REPO_USER_3, repo3.getId(), () -> createNode( NodePath.ROOT, "repo2Node" ) );
        refresh();

        final SearchTargets targets = SearchTargets.create().
            add( createTarget( MASTER_BRANCH, REPO_USER_1, repo1.getId() ) ).
            add( createTarget( MASTER_BRANCH, REPO_USER_2, repo2.getId() ) ).
            add( createTarget( MASTER_BRANCH, REPO_USER_1, repo3.getId() ) ). // repoUser1 has no access
            build();

        final NodeQuery query = NodeQuery.create().
            parent( NodePath.ROOT ).
            build();

        final FindNodesByMultiRepoQueryResult result = doQuery( query, targets );

        assertEquals( 2L, result.getTotalHits() );
        assertRepos( result, repo1.getId(), repo2.getId() );
        assertBranches( result, MASTER_BRANCH );
    }

    private void assertRepos( final FindNodesByMultiRepoQueryResult result, final RepositoryId... repositoryIds )
    {
        final MultiRepoNodeHits nodeHits = result.getNodeHits();
        final Set<RepositoryId> repositories = nodeHits.stream().map( MultiRepoNodeHit::getRepositoryId ).collect( Collectors.toSet() );
        assertEquals( repositoryIds.length, repositories.size(), "Wrong number of repositories" );
        for ( final RepositoryId repoId : repositoryIds )
        {
            assertTrue( repositories.contains( repoId ), "missing repo '" + repoId + "' in result set" );
        }
    }

    private void assertBranches( final FindNodesByMultiRepoQueryResult result, final Branch... branches )
    {
        final MultiRepoNodeHits nodeHits = result.getNodeHits();
        final Set<Branch> resultBranches = nodeHits.stream().map( MultiRepoNodeHit::getBranch ).collect( Collectors.toSet() );
        assertEquals( branches.length, resultBranches.size(), "Wrong number of branches in result" );
        for ( final Branch branch : resultBranches )
        {
            assertTrue( resultBranches.contains( branch ) , "missing repo '" + branch + "' in result set");
        }
    }

    private void assertNodes( final FindNodesByMultiRepoQueryResult result, final NodeId... nodeIds )
    {
        final MultiRepoNodeHits nodeHits = result.getNodeHits();

        for ( final NodeId nodeId : nodeIds )
        {
            boolean found = false;
            for ( final MultiRepoNodeHit nodeHit : nodeHits )
            {
                if ( nodeHit.getNodeId().equals( nodeId ) )
                {
                    found = true;
                    break;
                }
            }

            if ( !found )
            {
                fail( "Missing nodeId '" + nodeId + " in result" );
            }
        }
    }

    private FindNodesByMultiRepoQueryResult doQuery( final NodeQuery query, final SearchTargets targets )
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
        runInContext( runUser, repoId, MASTER_BRANCH, runnable );
    }

    private void runInContext( final User runUser, final RepositoryId repoId, final Branch branch, final Runnable runnable )
    {
        ContextBuilder.create().
            repositoryId( repoId ).
            branch( branch ).
            authInfo( AuthenticationInfo.create().user( runUser ).build() ).
            build().runWith( runnable );
    }

    private <T> T callInContext( final User runUser, final RepositoryId repoId, final Branch branch, final Callable<T> callable )
    {
        return ContextBuilder.create().
            repositoryId( repoId ).
            branch( branch ).
            authInfo( AuthenticationInfo.create().user( runUser ).build() ).
            build().callWith( callable );
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

    private Branch createBranch( final String branchName )
    {
        return NodeHelper.runAsAdmin( () -> this.repositoryService.createBranch( CreateBranchParams.from( branchName ) ) );
    }
}
