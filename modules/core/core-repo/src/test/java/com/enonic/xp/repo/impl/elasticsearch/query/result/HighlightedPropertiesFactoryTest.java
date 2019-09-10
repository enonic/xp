package com.enonic.xp.repo.impl.elasticsearch.query.result;

import java.util.Map;

import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.highlight.HighlightField;
import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.highlight.HighlightedProperties;
import com.enonic.xp.repo.impl.elasticsearch.result.HighlightedPropertiesFactory;

public class HighlightedPropertiesFactoryTest
{
    @Test
    public void create()
    {
        final HighlightField highlightField1 =
            new HighlightField( "name1", new Text[]{new Text( "fragment1_1" ), new Text( "fragment1_2" )} );

        final HighlightField highlightField2 =
            new HighlightField( "name2", new Text[]{new Text( "fragment2_1" ), new Text( "fragment2_2" )} );

        final Map<String, HighlightField> paramsMap = Map.of( "name1", highlightField1, "name2", highlightField2 );

        final HighlightedProperties highlightedProperties = HighlightedPropertiesFactory.create( paramsMap );

        Assert.assertNotNull( highlightedProperties );
        Assert.assertEquals( 2, highlightedProperties.size() );

        Assert.assertEquals( 2, highlightedProperties.get( "name1" ).getFragments().size() );
        Assert.assertTrue( highlightedProperties.get( "name1" ).getFragments().contains( "fragment1_1" ) );
        Assert.assertTrue( highlightedProperties.get( "name1" ).getFragments().contains( "fragment1_2" ) );

        Assert.assertEquals( 2, highlightedProperties.get( "name2" ).getFragments().size() );
        Assert.assertTrue( highlightedProperties.get( "name2" ).getFragments().contains( "fragment2_2" ) );
        Assert.assertTrue( highlightedProperties.get( "name2" ).getFragments().contains( "fragment2_1" ) );
    }

    @Test
    public void create_null()
    {
        final HighlightedProperties highlightedProperties = HighlightedPropertiesFactory.create( null );
        Assert.assertNull( highlightedProperties );
    }
}
