package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.Value;

class BooleanQueryBuilder
    extends DslQueryBuilder
{
    public static final String NAME = "boolean";

    BooleanQueryBuilder( final PropertySet expression )
    {
        super( expression );
    }

    public QueryBuilder create()
    {
        final BoolQueryBuilder builder = QueryBuilders.boolQuery();
        for ( final Property booleanProperty : getProperties() )
        {
            final Value queryValue = booleanProperty.getValue();

            if ( queryValue.isPropertySet() )
            {
                for ( final Property queryProperty : booleanProperty.getValue().asData().getProperties() )
                {
                    final QueryBuilder query = DslQueryParser.parseQuery( queryProperty );
                    switch ( booleanProperty.getName() )
                    {
                        case "must":
                            builder.must( query );
                            break;
                        case "should":
                            builder.should( query );
                            break;
                        case "mustNot":
                            builder.mustNot( query );
                            break;
                        default:
                            throw new IllegalArgumentException( "Invalid boolean expression: " + booleanProperty.getName() );
                    }
                }
            }
            else if ( queryValue.isNumericType() )
            {
                if ( "boost".equals( booleanProperty.getName() ) )
                {
                    builder.boost( queryValue.asDouble().floatValue() );
                }
                else
                {
                    throw new IllegalArgumentException(
                        "'" + booleanProperty.getName() + "' is not supported property for [boolean] query" );
                }
            }
            else
            {
                throw new IllegalArgumentException(
                    "[" + queryValue.getType().getName() + "] property type is not supported by [boolean] query" );
            }


        }
        return addBoost( builder, boost );
    }
}
