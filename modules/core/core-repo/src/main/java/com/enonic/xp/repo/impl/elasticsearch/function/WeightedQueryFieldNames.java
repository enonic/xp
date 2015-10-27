package com.enonic.xp.repo.impl.elasticsearch.function;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class WeightedQueryFieldNames
    implements Iterable<WeightedQueryFieldName>
{

    private final ImmutableList<WeightedQueryFieldName> weightedQueryFieldNames;

    private WeightedQueryFieldNames( final Collection<WeightedQueryFieldName> weightedQueryFieldNames )
    {
        this.weightedQueryFieldNames = ImmutableList.copyOf( weightedQueryFieldNames );
    }

    public ImmutableList<WeightedQueryFieldName> getWeightedQueryFieldNames()
    {
        return weightedQueryFieldNames;
    }

    @Override
    public Iterator<WeightedQueryFieldName> iterator()
    {
        return this.weightedQueryFieldNames.iterator();
    }

    public static WeightedQueryFieldNames from( final String weightedQueryFieldNamesString )
    {
        List<WeightedQueryFieldName> list = Lists.newArrayList();

        if ( Strings.isNullOrEmpty( weightedQueryFieldNamesString ) )
        {
            return null;
        }

        final String[] entries = weightedQueryFieldNamesString.split( "," );

        for ( final String entry : entries )
        {
            list.add( WeightedQueryFieldName.from( entry ) );
        }

        return new WeightedQueryFieldNames( list );
    }


}
