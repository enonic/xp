package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import java.time.ZoneOffset;

import org.elasticsearch.common.Strings;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.ValueTypeException;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.SearchQueryFieldNameResolver;
import com.enonic.xp.repo.impl.index.IndexValueType;

abstract class ExpressionQueryBuilder
    extends DslQueryBuilder
{
    private static final SearchQueryFieldNameResolver FIELD_NAME_RESOLVER = new SearchQueryFieldNameResolver();

    private final String field;

    private final String type;

    ExpressionQueryBuilder( final PropertySet expression )
    {
        super( expression );

        this.field = getString( "field" );
        this.type = getString( "type" );
    }

    protected Object parseValue( final Object value )
    {
        if ( type == null || value == null )
        {
            return value;
        }

        switch ( type )
        {
            case "geoPoint":
                return ValueTypes.GEO_POINT.convert( value );
            case "time":
                return ValueTypes.LOCAL_TIME.convert( value );
            case "date":
                return ValueTypes.LOCAL_DATE.convert( value );
            case "dateTime":
                try
                {
                    return ValueTypes.DATE_TIME.convert( value );
                }
                catch ( ValueTypeException e )
                {
                    return ValueTypes.LOCAL_DATE_TIME.convert( value ).toInstant( ZoneOffset.UTC );
                }
            default:
                throw new IllegalArgumentException( String.format( "There is no [%s] dsl expression type", type ) );
        }
    }

    protected String getFieldName( final Object value )
    {
        if ( Strings.isNullOrEmpty( type ) )
        {
            if ( value instanceof Number )
            {
                return FIELD_NAME_RESOLVER.resolve( field, IndexValueType.NUMBER );
            }
            return field;
        }

        switch ( type )
        {
            case "geoPoint":
                return FIELD_NAME_RESOLVER.resolve( field, IndexValueType.GEO_POINT );
            case "date":
            case "dateTime":
                return FIELD_NAME_RESOLVER.resolve( field, IndexValueType.DATETIME );
            case "time":
                return field;
            default:
                throw new IllegalArgumentException( String.format( "There is no [%s] dsl expression type", type ) );
        }
    }
}
