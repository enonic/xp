package com.enonic.xp.lib.node;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.FindNodesByMultiRepoQueryResult;
import com.enonic.xp.node.MultiRepoNodeHit;
import com.enonic.xp.node.MultiRepoNodeQuery;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.SearchTarget;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MultiRepoConnectTest
    extends BaseNodeHandlerTest
{
    @Test
    void testExample()
    {
        final Context context = ContextBuilder.create().
            authInfo( AuthenticationInfo.create().
                user( User.create().key( PrincipalKey.ofUser( IdProviderKey.system(), "test-user" ) ).login( "test-user" ).build() ).
                principals( RoleKeys.ADMIN ).
                build() ).
            build();

        context.runWith( () -> runScript( "/lib/xp/examples/node/multiRepoConnect.js" ) );
    }

    @Test
    void query()
    {
        Mockito.when( this.nodeService.findByQuery( Mockito.isA( MultiRepoNodeQuery.class ) ) ).thenReturn(
            FindNodesByMultiRepoQueryResult.create()
                .totalHits( 12902 )
                .addNodeHit( MultiRepoNodeHit.create()
                                 .branch( Branch.from( "master" ) )
                                 .repositoryId( RepositoryId.from( "my-repo" ) )
                                 .nodeId( NodeId.from( "b186d24f-ac38-42ca-a6db-1c1bda6c6c26" ) )
                                 .score( 1.23f )
                                 .build() )
                .addNodeHit( MultiRepoNodeHit.create()
                                 .branch( Branch.from( "draft" ) )
                                 .repositoryId( RepositoryId.from( "com.enonic.cms.default" ) )
                                 .nodeId( NodeId.from( "350ba4a6-589c-498b-8af0-f183850e1120" ) )
                                 .score( 1.40f )
                                 .build() )
                .build() );

        final ArgumentCaptor<MultiRepoNodeQuery> captor = ArgumentCaptor.forClass( MultiRepoNodeQuery.class );

        final Context context = ContextBuilder.create()
            .authInfo( AuthenticationInfo.create()
                           .user( User.create()
                                      .key( PrincipalKey.ofUser( IdProviderKey.system(), "test-user" ) )
                                      .login( "test-user" )
                                      .build() )
                           .principals( RoleKeys.ADMIN )
                           .build() )
            .build();

        context.runWith( () -> runFunction( "/test/MultiRepoConnectTest.js", "query" ) );

        Mockito.verify( nodeService ).findByQuery( captor.capture() );

        final MultiRepoNodeQuery query = captor.getValue();

        assertNotNull( query.getSearchTargets() );
        final List<SearchTarget> targets = new ArrayList<>();
        query.getSearchTargets().forEach( targets::add );
        assertEquals( 2, targets.size() );

        final SearchTarget firstTarget =
            targets.stream().filter( t -> t.getRepositoryId().equals( RepositoryId.from( "my-repo" ) ) ).findFirst().orElseThrow();
        assertEquals( Branch.from( "my-branch" ), firstTarget.getBranch() );
        assertEquals( PrincipalKey.ofRole( "system.admin" ), firstTarget.getPrincipalKeys().first() );

        final SearchTarget secondTarget =
            targets.stream().filter( t -> t.getRepositoryId().equals( RepositoryId.from( "my-other-repo" ) ) ).findFirst().orElseThrow();
        assertEquals( Branch.from( "master" ), secondTarget.getBranch() );
        assertEquals( PrincipalKey.ofRole( "system.admin" ), secondTarget.getPrincipalKeys().first() );

        assertNotNull( query.getNodeQuery() );
        assertEquals( 0, query.getNodeQuery().getFrom() );
        assertEquals( 2, query.getNodeQuery().getSize() );
        assertNotNull( query.getNodeQuery().getQuery() );
    }
}
