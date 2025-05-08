package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import static com.google.common.base.Strings.isNullOrEmpty;

public class WeightedQueryFieldNames
{
    private final ImmutableList<WeightedQueryFieldName> weightedQueryFieldNames;

    private WeightedQueryFieldNames( final Collection<WeightedQueryFieldName> weightedQueryFieldNames )
    {
        this.weightedQueryFieldNames = ImmutableList.copyOf( weightedQueryFieldNames );
    }

    public static WeightedQueryFieldNames from( final Collection<String> weightedQueryFieldNames )
    {
        return new WeightedQueryFieldNames(
            weightedQueryFieldNames.stream().map( WeightedQueryFieldName::from ).collect( Collectors.toList() ) );
    }

    public static WeightedQueryFieldNames from( final String weightedQueryFieldNamesString )
    {
        List<WeightedQueryFieldName> list = new ArrayList<>();

        if ( isNullOrEmpty( weightedQueryFieldNamesString ) )
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

    public List<WeightedQueryFieldName> getWeightedQueryFieldNames()
    {
        return weightedQueryFieldNames;
    }
}
