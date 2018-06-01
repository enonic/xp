package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import com.google.common.base.Strings;

import com.enonic.xp.data.Value;
import com.enonic.xp.query.QueryException;
import com.enonic.xp.query.expr.ValueExpr;

class RangeFunctionArgsFactory
{
    private static final int FIELDNAME_INDEX = 0;

    private static final int FROM_INDEX = 1;

    private static final int TO_INDEX = 2;

    private static final int INCLUDE_FROM_INDEX = 3;

    private static final int INCLUDE_TO_INDEX = 4;

    public static RangeFunctionArg create( final List<ValueExpr> arguments )
    {
        validateArguments( arguments );

        final String fieldName = arguments.get( FIELDNAME_INDEX ).getValue().asString();
        final ValueExpr from = arguments.get( FROM_INDEX );
        final ValueExpr to = arguments.get( TO_INDEX );

        boolean includeFrom = false;

        boolean includeTo = false;

        if ( arguments.size() >= 4 )
        {
            includeFrom = arguments.get( INCLUDE_FROM_INDEX ).getValue().asBoolean();
        }

        if ( arguments.size() >= 5 )
        {
            includeTo = arguments.get( INCLUDE_TO_INDEX ).getValue().asBoolean();
        }

        if ( isInstant( from.getValue(), to.getValue() ) || isInstantString( from.getValue(), to.getValue() ) )
        {
            return createInstantArgs( fieldName, from, to, includeFrom, includeTo );
        }

        if ( isNumeric( from.getValue(), to.getValue() ) )
        {
            return createNumericArgs( fieldName, from, to, includeFrom, includeTo );
        }

        return createStringArgs( fieldName, from, to, includeFrom, includeTo );
    }

    private static boolean isInstant( final Value value1, final Value value2 )
    {
        if ( value1 != null )
        {
            return value1.isDateType();
        }

        return value2.isDateType();
    }

    private static boolean isInstantIsoString( final Value value )
    {
        try
        {
            DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse( value.asString() );
            return true;
        }
        catch ( DateTimeParseException e )
        {
            return false;
        }
    }

    private static boolean isInstantString( final Value value1, final Value value2 )
    {
        if ( value1 != null )
        {
            return isInstantIsoString( value1 );
        }

        return isInstantIsoString( value2 );
    }

    private static boolean isNumeric( final Value value1, final Value value2 )
    {
        if ( value1 != null )
        {
            return value1.isNumericType();
        }

        return value2.isNumericType();
    }

    private static void validateArguments( final List<ValueExpr> arguments )
    {
        final int size = arguments.size();

        if ( size < 3 )
        {
            throw new QueryException( "Needs at least 3 arguments for range-function, got: [" + size + "]" );
        }
    }

    private static RangeFunctionArg createStringArgs( final String fieldName, final ValueExpr from, final ValueExpr to,
                                                      final boolean includeFrom, final boolean includeTo )
    {
        final StringRangeFunctionArg args = new StringRangeFunctionArg();
        args.setFieldName( fieldName );
        args.setFrom( isNullOrEmpty( from ) ? null : from.getValue().asString() );
        args.setTo( isNullOrEmpty( to ) ? null : to.getValue().asString() );
        args.setIncludeFrom( includeFrom );
        args.setIncludeTo( includeTo );

        return args;
    }

    private static RangeFunctionArg createNumericArgs( final String fieldName, final ValueExpr from, final ValueExpr to,
                                                       final boolean includeFrom, final boolean includeTo )
    {
        final NumericRangeFunctionArg args = new NumericRangeFunctionArg();
        args.setFieldName( fieldName );
        args.setFrom( isNullOrEmpty( from ) ? null : from.getValue().asDouble() );
        args.setTo( isNullOrEmpty( to ) ? null : to.getValue().asDouble() );
        args.setIncludeFrom( includeFrom );
        args.setIncludeTo( includeTo );

        return args;
    }

    private static RangeFunctionArg createInstantArgs( final String fieldName, final ValueExpr from, final ValueExpr to,
                                                       final boolean includeFrom, final boolean includeTo )
    {
        final InstantRangeFunctionArg args = new InstantRangeFunctionArg();
        args.setFieldName( fieldName );
        args.setFrom( isNullOrEmpty( from ) ? null : from.getValue().asInstant() );
        args.setTo( isNullOrEmpty( to ) ? null : to.getValue().asInstant() );
        args.setIncludeFrom( includeFrom );
        args.setIncludeTo( includeTo );

        return args;
    }


    private static boolean isNullOrEmpty( final ValueExpr valueExpr )
    {
        return valueExpr == null || valueExpr.getValue() == null || Strings.isNullOrEmpty( valueExpr.getValue().asString() );

    }

}
