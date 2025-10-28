package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory;

import org.junit.jupiter.api.Test;

import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function.WeightedQueryFieldName;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WeightedNodeIndexPathTest
{
    @Test
    void createWeightedQueryFieldName()
    {
        createWeightedQueryFieldName( "myField", "myField", null );
        createWeightedQueryFieldName( "myField^5", "myField", 5 );
        createWeightedQueryFieldName( "myField^", "myField", null );
        createWeightedQueryFieldName( "^5", "", 5 );
    }

    private void createWeightedQueryFieldName( final String value, final String baseFieldName, final Integer weight )
    {
        final WeightedQueryFieldName weightedQueryFieldName = WeightedQueryFieldName.from( value );

        assertEquals( baseFieldName, weightedQueryFieldName.getBaseFieldName() );
        assertEquals( weight, weightedQueryFieldName.getWeight() );
    }
}
