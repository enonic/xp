package com.enonic.wem.core.index.elastic.indexsource;

import org.junit.Test;

import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.entity.PropertyIndexConfig;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.index.document.IndexDocument2;
import com.enonic.wem.core.index.document.IndexDocumentItemFactory;

public class IndexSourceFactoryTest
{


    @Test
    public void testName()
        throws Exception
    {
        IndexDocument2 indexDocument2 = IndexDocument2.newIndexDocument().
            index( "wem" ).
            indexType( IndexType.NODE ).
            addEntries(
                IndexDocumentItemFactory.create( "myProperty", new Value.String( "test" ), PropertyIndexConfig.newPropertyIndexConfig().
                    autocompleteEnabled( true ).
                    fulltextEnabled( true ).
                    enabled( true ).
                    build() ) ).
            build();

        final IndexSource indexSource = IndexSourceFactory.create( indexDocument2 );


    }
}
