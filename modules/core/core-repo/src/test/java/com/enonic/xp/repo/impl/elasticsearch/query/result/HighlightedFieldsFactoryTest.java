package com.enonic.xp.repo.impl.elasticsearch.query.result;

import java.util.Map;

import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.highlight.HighlightField;
import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.highlight.HighlightedFields;
import com.enonic.xp.repo.impl.elasticsearch.result.HighlightedFieldsFactory;

public class HighlightedFieldsFactoryTest
{
    @Test
    public void create()
    {
        final HighlightField highlightField1 =
            new HighlightField( "name1", new Text[]{new Text( "fragment1_1" ), new Text( "fragment1_2" )} );

        final HighlightField highlightField2 =
            new HighlightField( "name2", new Text[]{new Text( "fragment2_1" ), new Text( "fragment2_2" )} );

        final Map<String, HighlightField> paramsMap = Map.of( "name1", highlightField1, "name2", highlightField2 );

        final HighlightedFields highlightedFields = HighlightedFieldsFactory.create( paramsMap );

        Assert.assertNotNull( highlightedFields );
        Assert.assertEquals( 2, highlightedFields.getHighlightedFields().size() );

        Assert.assertEquals( 2, highlightedFields.getHighlightedFields().get( "name1" ).getFragments().size() );
        Assert.assertTrue( highlightedFields.getHighlightedFields().get( "name1" ).getFragments().contains( "fragment1_1" ) );
        Assert.assertTrue( highlightedFields.getHighlightedFields().get( "name1" ).getFragments().contains( "fragment1_2" ) );

        Assert.assertEquals( 2, highlightedFields.getHighlightedFields().get( "name2" ).getFragments().size() );
        Assert.assertTrue( highlightedFields.getHighlightedFields().get( "name2" ).getFragments().contains( "fragment2_2" ) );
        Assert.assertTrue( highlightedFields.getHighlightedFields().get( "name2" ).getFragments().contains( "fragment2_1" ) );
    }

    @Test
    public void create_null()
    {
        final HighlightedFields highlightedFields = HighlightedFieldsFactory.create( null );
        Assert.assertNull( highlightedFields );
    }
}
