package com.enonic.wem.itests.core.entity;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.query.expr.CompareExpr;
import com.enonic.wem.api.query.expr.DynamicConstraintExpr;
import com.enonic.wem.api.query.expr.FieldExpr;
import com.enonic.wem.api.query.expr.FunctionExpr;
import com.enonic.wem.api.query.expr.QueryExpr;
import com.enonic.wem.api.query.expr.ValueExpr;
import com.enonic.wem.core.entity.FindNodesByQueryCommand;
import com.enonic.wem.repo.CreateNodeParams;
import com.enonic.wem.repo.FindNodesByQueryResult;
import com.enonic.wem.repo.Node;
import com.enonic.wem.repo.NodePath;
import com.enonic.wem.repo.NodeQuery;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;

public class FindNodesByQueryCommandTest
    extends AbstractNodeTest
{
    @Override
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
    }

    @Test
    public void get_by_parent()
        throws Exception
    {
        final Node node1 = createNode( CreateNodeParams.create().
            name( "my-node-1" ).
            parent( NodePath.ROOT ).
            build() );

        final Node node2 = createNode( CreateNodeParams.create().
            name( "my-node-2" ).
            parent( NodePath.ROOT ).
            build() );

        final Node childNode1 = createNode( CreateNodeParams.create().
            name( "child-node" ).
            parent( node1.path() ).
            build() );

        refresh();

        printContentRepoIndex();

        final NodeQuery query = NodeQuery.create().parent( NodePath.ROOT ).build();
        FindNodesByQueryResult result = doFindByQuery( query );
        assertEquals( 2, result.getNodes().getSize() );
        assertNotNull( result.getNodes().getNodeById( node1.id() ) );
        assertNotNull( result.getNodes().getNodeById( node2.id() ) );

        final NodeQuery childQuery = NodeQuery.create().parent( node1.path() ).build();
        result = doFindByQuery( childQuery );
        assertEquals( 1, result.getNodes().getSize() );
        assertNotNull( result.getNodes().getNodeById( childNode1.id() ) );
    }

    @Test
    public void compare_eq()
        throws Exception
    {
        final Node node1 = createNode( CreateNodeParams.create().
            name( "my-node-1" ).
            parent( NodePath.ROOT ).
            build() );

        createNode( CreateNodeParams.create().
            name( "my-node-2" ).
            parent( NodePath.ROOT ).
            build() );

        createNode( CreateNodeParams.create().
            name( "child-node" ).
            parent( node1.path() ).
            build() );

        final NodeQuery query = NodeQuery.create().
            query( QueryExpr.from( CompareExpr.eq( FieldExpr.from( "name" ), ValueExpr.string( "my-node-1" ) ) ) ).
            build();

        final FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 1, result.getNodes().getSize() );
        assertNotNull( result.getNodes().getNodeById( node1.id() ) );
    }

    @Test
    public void compare_gt()
        throws Exception
    {
        final RootDataSet data = new RootDataSet();
        data.setProperty( "my-value", Value.newDouble( 5 ) );

        createNode( CreateNodeParams.create().
            name( "my-node-1" ).
            parent( NodePath.ROOT ).
            data( data ).
            build() );

        final RootDataSet data2 = new RootDataSet();
        data2.setProperty( "my-value", Value.newDouble( 10 ) );

        final Node node2 = createNode( CreateNodeParams.create().
            name( "my-node-2" ).
            parent( NodePath.ROOT ).
            data( data2 ).
            build() );

        final NodeQuery query = NodeQuery.create().
            query( QueryExpr.from( CompareExpr.gt( FieldExpr.from( "my-value" ), ValueExpr.number( 7 ) ) ) ).
            build();

        final FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 1, result.getNodes().getSize() );
        assertNotNull( result.getNodes().getNodeById( node2.id() ) );
    }

    @Test
    public void fulltext()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "my-node-1" ).
            parent( NodePath.ROOT ).
            build() );

        refresh();

        final NodeQuery query = NodeQuery.create().
            query( QueryExpr.from( new DynamicConstraintExpr(
                FunctionExpr.from( "fulltext", ValueExpr.string( "name" ), ValueExpr.string( "My node name is my-node-1" ),
                                   ValueExpr.string( "OR" ) ) ) ) ).
            build();

        final FindNodesByQueryResult result = doFindByQuery( query );

        assertEquals( 1, result.getNodes().getSize() );
        assertNotNull( result.getNodes().getNodeById( node.id() ) );
    }

    private FindNodesByQueryResult doFindByQuery( final NodeQuery query )
    {
        return FindNodesByQueryCommand.create().
            query( query ).
            queryService( this.queryService ).
            versionService( this.versionService ).
            workspaceService( this.workspaceService ).
            nodeDao( this.nodeDao ).
            indexService( this.indexService ).
            build().
            execute();
    }
}
