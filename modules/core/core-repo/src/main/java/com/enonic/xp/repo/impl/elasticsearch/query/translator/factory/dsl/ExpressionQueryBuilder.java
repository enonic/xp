package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.dsl;

import java.time.ZoneOffset;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.ValueTypeException;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.SearchQueryFieldNameResolver;
import com.enonic.xp.repo.impl.index.IndexValueType;

import static com.google.common.base.Strings.nullToEmpty;

abstract class ExpressionQueryBuilder
    extends DslQueryBuilder
{
    private static final SearchQueryFieldNameResolver FIELD_NAME_RESOLVER = new SearchQueryFieldNameResolver();

    protected final String type;

    protected final String field;

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
            case "time":
                return ValueTypes.LOCAL_TIME.convert( value );
            case "dateTime":

                try
                {
                    return ValueTypes.DATE_TIME.convert( value );
                }
                catch ( ValueTypeException dtEx )
                {
                    try
                    {
                        return ValueTypes.LOCAL_DATE_TIME.convert( value ).toInstant( ZoneOffset.UTC );
                    }
                    catch ( ValueTypeException ldtEx )
                    {
                        try
                        {
                            return ValueTypes.LOCAL_DATE.convert( value ).atStartOfDay().toInstant( ZoneOffset.UTC );
                        }
                        catch ( ValueTypeException ldEx )
                        {
                            throw new IllegalArgumentException( "value must be in either Date, DateTime or LocalDateTime format" );
                        }
                    }
                }
            default:
                throw new IllegalArgumentException( String.format( "There is no [%s] dsl expression type", type ) );
        }
    }

    protected String getFieldName( final Object value )
    {
        if ( nullToEmpty( field ).isBlank() )
        {
            return null;
        }

        if ( nullToEmpty( type ).isBlank() )
        {
            if ( value instanceof Number )
            {
                return FIELD_NAME_RESOLVER.resolve( field, IndexValueType.NUMBER );
            }
            return FIELD_NAME_RESOLVER.resolve( field );
        }

        switch ( type )
        {
            case "dateTime":
                return FIELD_NAME_RESOLVER.resolve( field, IndexValueType.DATETIME );
            case "time":
                return FIELD_NAME_RESOLVER.resolve( field );
            default:
                throw new IllegalArgumentException( String.format( "There is no [%s] dsl expression type", type ) );
        }
    }
}
