package com.enonic.wem.itests.core.entity;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.core.entity.CreateNodeParams;
import com.enonic.wem.core.entity.DuplicateNodeCommand;
import com.enonic.wem.core.entity.DuplicateValueResolver;
import com.enonic.wem.core.entity.FindNodesByParentParams;
import com.enonic.wem.core.entity.FindNodesByParentResult;
import com.enonic.wem.core.entity.Node;
import com.enonic.wem.core.entity.NodePath;
import com.enonic.wem.core.entity.Nodes;

import static org.junit.Assert.*;

public class DuplicateNodeCommandTest
    extends AbstractNodeTest
{

    @Override
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();

        createContentRepository();
    }

    @Test
    public void duplicate_single()
        throws Exception
    {
        final String nodeName = "my-node";
        final Node createdNode = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( nodeName ).
            build() );

        final Node duplicatedNode = DuplicateNodeCommand.create().
            id( createdNode.id() ).
            versionService( versionService ).
            indexService( indexService ).
            nodeDao( nodeDao ).
            queryService( queryService ).
            workspaceService( workspaceService ).
            build().
            execute();

        assertEquals( nodeName + "-" + DuplicateValueResolver.COPY_TOKEN, duplicatedNode.name().toString() );
    }

    @Test
    public void duplicate_with_children()
        throws Exception
    {
        final String nodeName = "my-node";
        final Node createdNode = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( nodeName ).
            build() );

        final Node childNode = createNode( CreateNodeParams.create().
            parent( createdNode.path() ).
            name( "my-child" ).
            build() );

        refresh();

        final Node duplicatedNode = DuplicateNodeCommand.create().
            id( createdNode.id() ).
            versionService( versionService ).
            indexService( indexService ).
            nodeDao( nodeDao ).
            queryService( queryService ).
            workspaceService( workspaceService ).
            build().
            execute();

        refresh();

        final FindNodesByParentResult children = findByParent( FindNodesByParentParams.create().
            parentPath( duplicatedNode.path() ).
            build() );

        final Nodes childNodes = children.getNodes();
        assertEquals( 1, childNodes.getSize() );
        assertEquals( childNode.name(), childNodes.first().name() );
    }

}
