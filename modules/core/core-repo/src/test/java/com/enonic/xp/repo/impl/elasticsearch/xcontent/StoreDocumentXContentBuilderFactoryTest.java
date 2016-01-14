package com.enonic.xp.repo.impl.elasticsearch.xcontent;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.junit.Test;

import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.elasticsearch.document.IndexDocument;
import com.enonic.xp.repo.impl.elasticsearch.document.indexitem.IndexItems;

public class StoreDocumentXContentBuilderFactoryTest
{

    @Test
    public void testName()
        throws Exception
    {
        final IndexItems indexItems = IndexItems.create().
            add( "myProperty", ValueFactory.newString( "myValue" ), IndexConfig.MINIMAL ).
            build();

        final IndexDocument indexDocument = IndexDocument.create().
            analyzer( "myAnalyzer" ).
            id( NodeId.from( "myNodeId" ).toString() ).
            indexName( "myIndex" ).
            indexTypeName( "myIndexType" ).
            indexItems( indexItems ).
            build();

        final XContentBuilder xContentBuilder = StoreDocumentXContentBuilderFactory.create( indexDocument );

        System.out.println( xContentBuilder.string() );

    }
}