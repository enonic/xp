package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory;

import org.junit.jupiter.api.Test;

import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function.WeightedQueryFieldName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WeightedNodeIndexPathTest
{
    @Test
    void createWeightedQueryFieldName()
    {
        createWeightedQueryFieldName( "myField", "myField", null );
        createWeightedQueryFieldName( "myField^0.5", "myField", 0.5f );
        createWeightedQueryFieldName( "myField^5", "myField", 5f );
        createWeightedQueryFieldName( "myField^", "myField", null );
        createWeightedQueryFieldName( "^5", "", 5f );
    }

    private void createWeightedQueryFieldName( final String value, final String baseFieldName, final Float weight )
    {
        final WeightedQueryFieldName weightedQueryFieldName = WeightedQueryFieldName.from( value );

        assertEquals( baseFieldName, weightedQueryFieldName.getBaseFieldName() );
        assertEquals( weight, weightedQueryFieldName.getWeight() );
    }

    @Test
    void invalidWeight()
    {
        assertThrows( IllegalArgumentException.class, () -> WeightedQueryFieldName.from( "myField^abc" ) );
        assertThrows( IllegalArgumentException.class, () -> WeightedQueryFieldName.from( "myField^-1" ) );
    }
}
