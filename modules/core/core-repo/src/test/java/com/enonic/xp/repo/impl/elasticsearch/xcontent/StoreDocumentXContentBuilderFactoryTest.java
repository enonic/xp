package com.enonic.xp.repo.impl.elasticsearch.xcontent;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.junit.jupiter.api.Test;

import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.elasticsearch.document.IndexDocument;
import com.enonic.xp.repo.impl.elasticsearch.document.indexitem.IndexItems;

class StoreDocumentXContentBuilderFactoryTest
{

    @Test
    void testName()
        throws Exception
    {
        final IndexItems indexItems = IndexItems.create().
            add( IndexPath.from( "myProperty" ), ValueFactory.newString( "myValue" ), createDefaultDocument( IndexConfig.MINIMAL ) ).
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

    private IndexConfigDocument createDefaultDocument( final IndexConfig indexConfig )
    {
        final PatternIndexConfigDocument.Builder builder = PatternIndexConfigDocument.create().defaultConfig( indexConfig );
        return builder.build();
    }
}
