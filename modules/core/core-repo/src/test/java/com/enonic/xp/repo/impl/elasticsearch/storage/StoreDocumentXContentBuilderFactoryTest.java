package com.enonic.xp.repo.impl.elasticsearch.storage;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.junit.jupiter.api.Test;

import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.elasticsearch.document.IndexDocument;
import com.enonic.xp.repo.impl.elasticsearch.document.indexitem.IndexItemFactory;
import com.enonic.xp.repo.impl.elasticsearch.document.indexitem.IndexItems;

class StoreDocumentXContentBuilderFactoryTest
{

    @Test
    void testName()
        throws Exception
    {
        final IndexItems indexItems = IndexItems.create()
            .add( IndexItemFactory.create( IndexPath.from( "myProperty" ), ValueFactory.newString( "myValue" ),
                                           createDefaultDocument( IndexConfig.MINIMAL ) ) )
            .build();

        final IndexDocument indexDocument = new IndexDocument( NodeId.from( "myNodeId" ).toString(), indexItems, "myAnalyzer" );

        final XContentBuilder xContentBuilder = XContentBuilderFactory.create( indexDocument );

        System.out.println( xContentBuilder.string() );

    }

    private IndexConfigDocument createDefaultDocument( final IndexConfig indexConfig )
    {
        final PatternIndexConfigDocument.Builder builder = PatternIndexConfigDocument.create().defaultConfig( indexConfig );
        return builder.build();
    }
}
