package com.enonic.xp.repo.impl.elasticsearch.query.result;

import java.util.Map;

import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.highlight.HighlightField;
import org.junit.jupiter.api.Test;

import com.enonic.xp.highlight.HighlightedProperties;
import com.enonic.xp.repo.impl.elasticsearch.result.HighlightedPropertiesFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HighlightedPropertiesFactoryTest
{
    @Test
    void create()
    {
        final HighlightField highlightField1 =
            new HighlightField( "name1", new Text[]{new Text( "fragment1_1" ), new Text( "fragment1_2" )} );

        final HighlightField highlightField2 =
            new HighlightField( "name2", new Text[]{new Text( "fragment2_1" ), new Text( "fragment2_2" )} );

        final Map<String, HighlightField> paramsMap = Map.of( "name1", highlightField1, "name2", highlightField2 );

        final HighlightedProperties highlightedProperties = HighlightedPropertiesFactory.create( paramsMap );

        assertNotNull( highlightedProperties );
        assertEquals( 2, highlightedProperties.size() );

        assertEquals( 2, highlightedProperties.get( "name1" ).getFragments().size() );
        assertTrue( highlightedProperties.get( "name1" ).getFragments().contains( "fragment1_1" ) );
        assertTrue( highlightedProperties.get( "name1" ).getFragments().contains( "fragment1_2" ) );

        assertEquals( 2, highlightedProperties.get( "name2" ).getFragments().size() );
        assertTrue( highlightedProperties.get( "name2" ).getFragments().contains( "fragment2_2" ) );
        assertTrue( highlightedProperties.get( "name2" ).getFragments().contains( "fragment2_1" ) );
    }

    @Test
    void create_null()
    {
        final HighlightedProperties highlightedProperties = HighlightedPropertiesFactory.create( null );
        assertNull( highlightedProperties );
    }
}
