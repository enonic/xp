package com.enonic.xp.repo.impl.node;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.MultiRepoNodeQuery;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.SearchTarget;
import com.enonic.xp.node.SearchTargets;
import com.enonic.xp.repo.impl.MultiRepoSearchSource;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repo.impl.storage.NodeStorageService;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FindNodesByMultiRepoQueryCommandTest
{
    private static final PrincipalKey USER_KEY = PrincipalKey.ofUser( IdProviderKey.system(), "testuser" );

    private static final PrincipalKeys CONTEXT_PRINCIPALS = PrincipalKeys.from( USER_KEY, RoleKeys.EVERYONE );

    private final Context context = ContextBuilder.create().
        repositoryId( RepositoryId.from( "my-repo" ) ).
        branch( Branch.from( "master" ) ).
        authInfo( AuthenticationInfo.create().
            user( User.create().key( USER_KEY ).login( "testuser" ).build() ).
            principals( CONTEXT_PRINCIPALS ).
            build() ).
        build();

    @Test
    void explicit_principalKeys_used()
    {
        final PrincipalKeys explicitKeys = PrincipalKeys.from( RoleKeys.ADMIN );

        final SearchTargets targets = SearchTargets.create().
            add( SearchTarget.create().
                repositoryId( RepositoryId.from( "my-repo" ) ).
                branch( Branch.from( "master" ) ).
                principalKeys( explicitKeys ).
                build() ).
            build();

        final SingleRepoSearchSource source = context.callWith( () -> executeAndCaptureSource( targets ) );

        assertEquals( explicitKeys, source.getAcl() );
    }

    @Test
    void context_principalKeys_used_when_not_set()
    {
        final SearchTargets targets = SearchTargets.create().
            add( SearchTarget.create().
                repositoryId( RepositoryId.from( "my-repo" ) ).
                branch( Branch.from( "master" ) ).
                build() ).
            build();

        final SingleRepoSearchSource source = context.callWith( () -> executeAndCaptureSource( targets ) );

        assertEquals( CONTEXT_PRINCIPALS, source.getAcl() );
    }

    private SingleRepoSearchSource executeAndCaptureSource( final SearchTargets targets )
    {
        final NodeSearchService nodeSearchService = mock( NodeSearchService.class );
        final SearchResult searchResult = SearchResult.create().hits( List.of() ).build();
        when( nodeSearchService.query( any( NodeQuery.class ), any( MultiRepoSearchSource.class ) ) ).thenReturn( searchResult );

        final MultiRepoNodeQuery query = new MultiRepoNodeQuery( targets, NodeQuery.create().build() );

        FindNodesByMultiRepoQueryCommand.create().
            query( query ).
            indexServiceInternal( mock( IndexServiceInternal.class ) ).
            storageService( mock( NodeStorageService.class ) ).
            searchService( nodeSearchService ).
            build().
            execute();

        final ArgumentCaptor<MultiRepoSearchSource> captor = ArgumentCaptor.forClass( MultiRepoSearchSource.class );
        verify( nodeSearchService ).query( any( NodeQuery.class ), captor.capture() );

        return captor.getValue().getSources().iterator().next();
    }
}
