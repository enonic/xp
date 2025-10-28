package com.enonic.xp.repo.impl.elasticsearch;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.elasticsearch.document.IndexDocument;
import com.enonic.xp.repo.impl.elasticsearch.document.indexitem.IndexItems;
import com.enonic.xp.repo.impl.elasticsearch.document.indexitem.IndexValue;
import com.enonic.xp.repo.impl.elasticsearch.document.indexitem.IndexValueString;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.util.Reference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NodeStoreDocumentFactoryTest
{
    @Test
    void references()
    {
        final PropertyTree data = new PropertyTree();
        data.addReference( "myRef", new Reference( NodeId.from( "otherNode" ) ) );

        final Node node = Node.create().
            id( NodeId.from( "myNodeId" ) ).
            parentPath( NodePath.ROOT ).
            name( "myNode" ).
            data( data ).
            build();

        final IndexDocument indexDocument = NodeStoreDocumentFactory.createBuilder()
            .node( node )
            .branch( Branch.from( "myBranch" ) )
            .repositoryId( RepositoryId.from( "my-repo" ) )
            .build()
            .create();

        final IndexItems indexItems = indexDocument.getIndexItems();
        final Collection<IndexValue> referenceValues = indexItems.get( NodeIndexPath.REFERENCE.getPath() );
        assertEquals( 1, referenceValues.size() );
        final IndexValue next = referenceValues.iterator().next();
        assertTrue( next instanceof IndexValueString );
        final IndexValueString referenceValue = (IndexValueString) next;
        assertEquals( "otherNode", referenceValue.getValue() );
    }
}
