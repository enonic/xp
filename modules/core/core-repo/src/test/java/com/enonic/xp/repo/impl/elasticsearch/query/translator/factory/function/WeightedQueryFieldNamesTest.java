package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        final List<WeightedQueryFieldName> entries = weightedQueryFieldNames.getWeightedQueryFieldNames();

        assertNotNull( entries );
        assertEquals( numEntries, entries.size() );
    }
}
