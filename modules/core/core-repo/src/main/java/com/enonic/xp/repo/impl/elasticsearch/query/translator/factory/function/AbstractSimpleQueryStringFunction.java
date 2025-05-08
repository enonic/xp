package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

import org.elasticsearch.index.query.SimpleQueryStringBuilder;

abstract class AbstractSimpleQueryStringFunction
{

    static void appendQueryFieldNames( final AbstractSimpleQueryStringFunctionArguments arguments, final SimpleQueryStringBuilder builder )
    {
        for ( final WeightedQueryFieldName weightedQueryFieldName : arguments.getWeightedQueryFieldName().getWeightedQueryFieldNames() )
        {
            final String queryFieldName = arguments.resolveQueryFieldName( weightedQueryFieldName.getBaseFieldName() );

            if ( weightedQueryFieldName.getWeight() != null )
            {
                builder.field( queryFieldName, weightedQueryFieldName.getWeight() );
            }
            else
            {
                builder.field( queryFieldName );
            }
        }
    }

}
