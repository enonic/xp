package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

import static com.google.common.base.Strings.isNullOrEmpty;

public class WeightedQueryFieldName
{
    private final String baseFieldName;

    private final Integer weight;

    private WeightedQueryFieldName( final String baseFieldName, final Integer weight )
    {
        this.baseFieldName = baseFieldName;
        this.weight = weight;
    }

    public String getBaseFieldName()
    {
        return baseFieldName;
    }

    public Integer getWeight()
    {
        return weight;
    }

    public static WeightedQueryFieldName from( final String value )
    {
        if ( isNullOrEmpty( value ) )
        {
            return null;
        }

        final String[] split = value.split( "\\^" );

        if ( split.length > 1 )
        {
            return new WeightedQueryFieldName( split[0], Integer.parseInt( split[1] ) );
        }
        else
        {
            return new WeightedQueryFieldName( split[0], null );
        }
    }
}

