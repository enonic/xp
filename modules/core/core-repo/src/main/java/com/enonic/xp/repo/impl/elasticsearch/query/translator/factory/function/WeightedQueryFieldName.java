package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

public class WeightedQueryFieldName
{
    private final String baseFieldName;

    private final Float weight;

    private WeightedQueryFieldName( final String baseFieldName, final Float weight )
    {
        this.baseFieldName = baseFieldName;
        this.weight = weight;
    }

    public String getBaseFieldName()
    {
        return baseFieldName;
    }

    public Float getWeight()
    {
        return weight;
    }

    public static WeightedQueryFieldName from( final String value )
    {
        final String[] split = value.split( "\\^" );

        if ( split.length > 1 )
        {
            final String field = split[0];
            final float weight = Float.parseFloat( split[1] );
            if ( !Float.isFinite( weight ) || weight < 0 )
            {
                throw new IllegalArgumentException( "Invalid weight" );
            }
            return new WeightedQueryFieldName( field, weight );
        }
        else
        {
            return new WeightedQueryFieldName( split[0], null );
        }
    }
}

