package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;

import static org.junit.jupiter.api.Assertions.*;

public class WeightedQueryFieldNamesTest
{
    @Test
    public void createWeightedQueryFieldNames()
    {
        createWeightedQueryFieldName( "myfied", 1 );
        createWeightedQueryFieldName( "myfield^5", 1 );
        createWeightedQueryFieldName( "myfield^5,myotherfield", 2 );
        createWeightedQueryFieldName( "myfield,myotherfield^2", 2 );
    }

    private void createWeightedQueryFieldName( final String value, final int numEntries )
    {
        final WeightedQueryFieldNames weightedQueryFieldNames = WeightedQueryFieldNames.from( value );
        final ImmutableList<WeightedQueryFieldName> entries = weightedQueryFieldNames.getWeightedQueryFieldNames();

        assertNotNull( entries );
        assertEquals( numEntries, entries.size() );
    }
}
