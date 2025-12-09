package com.enonic.xp.repo.impl.elasticsearch.document.indexitem;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.repo.impl.index.IndexValueType;

import static org.assertj.core.api.Assertions.assertThat;

class IndexItemsTest
{
    @Test
    void single_string()
    {
        final IndexItems indexItems = IndexItems.create()
            .add( IndexPath.from( "myItem" ), ValueFactory.newString( "ost" ), createDefaultDocument( IndexConfig.MINIMAL ) )
            .build();

        final Collection<Object> values = indexItems.asValuesMap().get( "myitem" );

        assertThat( values ).containsExactly( "ost" );
    }

    @Test
    void multiple_strings()
    {
        final IndexItems indexItems = IndexItems.create()
            .add( IndexPath.from( "myItem" ), ValueFactory.newString( "ost" ), createDefaultDocument( IndexConfig.MINIMAL ) )
            .add( IndexPath.from( "myItem" ), ValueFactory.newString( "fisk" ), createDefaultDocument( IndexConfig.MINIMAL ) )
            .build();

        final Collection<Object> values = indexItems.asValuesMap().get( "myitem" );

        assertThat( values ).containsExactly( "ost", "fisk" );
    }


    @Test
    void single_orderby_value()
    {
        final IndexItems indexItems = IndexItems.create()
            .add( IndexPath.from( "myItem" ), ValueFactory.newString( "ost" ), createDefaultDocument( IndexConfig.MINIMAL ) )
            .add( IndexPath.from( "myItem" ), ValueFactory.newString( "fisk" ), createDefaultDocument( IndexConfig.MINIMAL ) )
            .build();

        final Collection<Object> values =
            indexItems.asValuesMap().get( "myitem" + IndexItem.INDEX_VALUE_TYPE_SEPARATOR + IndexValueType.ORDERBY.getPostfix() );

        assertThat( values ).containsExactly( "ost" );
    }

    private IndexConfigDocument createDefaultDocument( final IndexConfig indexConfig )
    {
        final PatternIndexConfigDocument.Builder builder = PatternIndexConfigDocument.create().defaultConfig( indexConfig );
        return builder.build();
    }
}
