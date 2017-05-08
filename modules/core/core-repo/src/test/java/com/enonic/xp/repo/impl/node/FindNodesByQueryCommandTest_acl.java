package com.enonic.xp.repo.impl.node;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.DynamicConstraintExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.FunctionExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static org.junit.Assert.*;

public class FindNodesByQueryCommandTest_acl
    extends AbstractNodeTest
{
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.createDefaultRootNode();
    }

    @Test
    public void get_by_parent_filter_acl()
        throws Exception
    {
        final Node parentNode = createNode( CreateNodeParams.create().
            name( "my-node-1" ).
            parent( NodePath.ROOT ).
            build() );

        // With access
        createNode( CreateNodeParams.create().
            name( "child-node1" ).
            parent( parentNode.path() ).
            build() );

        // no access
        createNode( CreateNodeParams.create().
            name( "child-node2" ).
            parent( parentNode.path() ).
            permissions( denyReadForPrincipal( TEST_DEFAULT_USER.getKey() ) ).
            build() );

        refresh();

        final NodeQuery query = NodeQuery.create().parent( parentNode.path() ).build();

        assertEquals( 1L, doFindByQuery( query ).getTotalHits() );
        assertEquals( 2L, NodeHelper.runAsAdmin( () -> doFindByQuery( query ) ).getTotalHits() );
    }


    @Test
    public void fulltext_with_acl()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "node_with_access" ).
            parent( NodePath.ROOT ).
            indexConfigDocument( PatternIndexConfigDocument.create().
                analyzer( NodeConstants.DOCUMENT_INDEX_DEFAULT_ANALYZER ).
                defaultConfig( IndexConfig.BY_TYPE ).
                build() ).
            build() );

        final Node nodeNodeAccess = createNode( CreateNodeParams.create().
            name( "my-node-2" ).
            parent( NodePath.ROOT ).
            permissions( denyReadForPrincipal( TEST_DEFAULT_USER.getKey() ) ).
            indexConfigDocument( PatternIndexConfigDocument.create().
                analyzer( NodeConstants.DOCUMENT_INDEX_DEFAULT_ANALYZER ).
                defaultConfig( IndexConfig.BY_TYPE ).
                build() ).
            build() );

        final NodeQuery query = NodeQuery.create().
            query( QueryExpr.from( new DynamicConstraintExpr(
                FunctionExpr.from( "fulltext", ValueExpr.string( NodeIndexPath.NAME.getPath() ),
                                   ValueExpr.string( "My node name is my-node-1" ), ValueExpr.string( "OR" ) ) ) ) ).
            build();

        final FindNodesByQueryResult resultWithTestUser = doFindByQuery( query );
        assertEquals( 1L, resultWithTestUser.getTotalHits() );
        assertTrue( resultWithTestUser.getNodeIds().contains( node.id() ) );

        final FindNodesByQueryResult resultAsAdmin = NodeHelper.runAsAdmin( () -> doFindByQuery( query ) );
        assertEquals( 2L, resultAsAdmin.getTotalHits() );
        assertTrue( resultAsAdmin.getNodeIds().contains( node.id() ) );
        assertTrue( resultAsAdmin.getNodeIds().contains( nodeNodeAccess.id() ) );
    }


    private AccessControlList denyReadForPrincipal( final PrincipalKey principalKey )
    {
        return AccessControlList.of( AccessControlEntry.create().deny( Permission.READ ).principal( principalKey ).build() );
    }


    private void queryAndAssert( final String path1, final String value1, final Node node1 )
    {
        final NodeQuery query = NodeQuery.create().
            query( QueryExpr.from( CompareExpr.eq( FieldExpr.from( path1 ), ValueExpr.string( value1 ) ) ) ).
            build();

        final FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 1, result.getNodeIds().getSize() );
        assertTrue( result.getNodeIds().contains( node1.id() ) );
    }

}
