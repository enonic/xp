package com.enonic.xp.repo.impl.elasticsearch.query.translator;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.repo.impl.elasticsearch.function.WeightedQueryFieldName;
import com.enonic.xp.repo.impl.elasticsearch.function.WeightedQueryFieldNames;

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

        Assert.assertNotNull( entries );
        Assert.assertEquals( numEntries, entries.size() );
    }
}
