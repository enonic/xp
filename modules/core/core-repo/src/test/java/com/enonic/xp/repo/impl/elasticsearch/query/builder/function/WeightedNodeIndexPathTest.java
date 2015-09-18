package com.enonic.xp.repo.impl.elasticsearch.query.builder.function;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.xp.repo.impl.elasticsearch.function.WeightedQueryFieldName;

public class WeightedNodeIndexPathTest
{
    @Test
    public void createWeightedQueryFieldName()
    {
        createWeightedQueryFieldName( "myField", "myField", null );
        createWeightedQueryFieldName( "myField^5", "myField", 5 );
        createWeightedQueryFieldName( "myField^", "myField", null );
        createWeightedQueryFieldName( "^5", "", 5 );
    }

    private void createWeightedQueryFieldName( final String value, final String baseFieldName, final Integer weight )
    {
        final WeightedQueryFieldName weightedQueryFieldName = WeightedQueryFieldName.from( value );

        Assert.assertEquals( baseFieldName, weightedQueryFieldName.getBaseFieldName() );
        Assert.assertEquals( weight, weightedQueryFieldName.getWeight() );
    }
}
