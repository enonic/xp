package com.enonic.xp.repo.impl.search.dsl;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyArray;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.Value;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.ConstraintExpr;
import com.enonic.xp.query.expr.DslExpr;
import com.enonic.xp.query.expr.DynamicConstraintExpr;
import com.enonic.xp.query.expr.Expression;
import com.enonic.xp.query.expr.FunctionExpr;
import com.enonic.xp.query.expr.LogicalExpr;
import com.enonic.xp.query.expr.NotExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.index.IndexPath;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Renders a {@code ConstraintExpr} tree into the canonical JSON query DSL.
 * <p>
 * Canonicalization rules that are part of the format: an absent constraint becomes
 * {@code matchAll}; logical nesting is preserved pairwise left-associative and never
 * flattened; numerics are JSON numbers with {@code Double} semantics, and are stringified
 * where the AST puts them on a string field; field names are emitted post-{@code IndexPath}
 * without sub-field postfixes (postfix resolution belongs to the backend); no
 * {@code queryName} is carried.
 */
final class ConstraintDslRenderer
{
    private ConstraintDslRenderer()
    {
    }

    static Map<String, Object> render( final Expression constraint )
    {
        if ( constraint == null )
        {
            return wrap( "matchAll", new LinkedHashMap<>() );
        }
        else if ( constraint instanceof LogicalExpr )
        {
            return renderLogical( (LogicalExpr) constraint );
        }
        else if ( constraint instanceof DynamicConstraintExpr )
        {
            return renderFunction( ( (DynamicConstraintExpr) constraint ).getFunction() );
        }
        else if ( constraint instanceof CompareExpr )
        {
            return renderCompare( (CompareExpr) constraint );
        }
        else if ( constraint instanceof NotExpr )
        {
            return mustNot( render( ( (NotExpr) constraint ).getExpression() ) );
        }
        else if ( constraint instanceof DslExpr )
        {
            return renderDsl( (DslExpr) constraint );
        }
        throw new DslRenderException( "Not able to render expression of type " + constraint.getClass() );
    }

    private static Map<String, Object> renderLogical( final LogicalExpr expr )
    {
        final String clause = expr.getOperator() == LogicalExpr.Operator.OR ? "should" : "must";

        final List<Object> operands = new ArrayList<>( 2 );
        operands.add( render( expr.getLeft() ) );
        operands.add( render( expr.getRight() ) );

        final Map<String, Object> bool = new LinkedHashMap<>();
        bool.put( clause, operands );
        return wrap( "boolean", bool );
    }

    private static Map<String, Object> mustNot( final Map<String, Object> inner )
    {
        final Map<String, Object> bool = new LinkedHashMap<>();
        bool.put( "mustNot", inner );
        return wrap( "boolean", bool );
    }

    private static Map<String, Object> renderCompare( final CompareExpr expr )
    {
        switch ( expr.getOperator() )
        {
            case EQ:
                return renderTerm( expr );
            case NEQ:
                return mustNot( renderTerm( expr ) );
            case GT:
                return renderRangeCompare( expr, "gt" );
            case GTE:
                return renderRangeCompare( expr, "gte" );
            case LT:
                return renderRangeCompare( expr, "lt" );
            case LTE:
                return renderRangeCompare( expr, "lte" );
            case LIKE:
                return renderLike( expr );
            case NOT_LIKE:
                return mustNot( renderLike( expr ) );
            case IN:
                return renderIn( expr );
            case NOT_IN:
                return mustNot( renderIn( expr ) );
            default:
                throw new DslRenderException( "Operator " + expr.getOperator() + " is not supported" );
        }
    }

    private static Map<String, Object> renderTerm( final CompareExpr expr )
    {
        final Value value = firstValue( expr );

        final Map<String, Object> term = new LinkedHashMap<>();
        term.put( "field", fieldName( expr ) );
        if ( value.isDateType() )
        {
            term.put( "type", "dateTime" );
        }
        term.put( "value", scalar( value ) );
        return wrap( "term", term );
    }

    private static Map<String, Object> renderRangeCompare( final CompareExpr expr, final String bound )
    {
        final Value value = firstValue( expr );

        final Map<String, Object> range = new LinkedHashMap<>();
        range.put( "field", fieldName( expr ) );
        if ( value.isDateType() )
        {
            range.put( "type", "dateTime" );
        }
        range.put( bound, scalar( value ) );
        return wrap( "range", range );
    }

    private static Map<String, Object> renderLike( final CompareExpr expr )
    {
        final Value value = firstValue( expr );
        rejectGeoPoint( value );

        final Map<String, Object> like = new LinkedHashMap<>();
        like.put( "field", fieldName( expr ) );
        like.put( "value", value.asString() );
        return wrap( "like", like );
    }

    /**
     * {@code IN} resolves the sub-field per value type, unlike the NoQL expression tree which
     * pins the field to the base string field while still type-converting the value — the
     * latent bug behind gap G-2, ruled to be fixed rather than preserved.
     */
    private static Map<String, Object> renderIn( final CompareExpr expr )
    {
        final List<ValueExpr> values = expr.getValues();
        if ( values.isEmpty() )
        {
            throw new DslRenderException( "Cannot render empty 'IN' statements" );
        }

        int dated = 0;
        for ( final ValueExpr value : values )
        {
            rejectGeoPoint( value.getValue() );
            if ( value.getValue().isDateType() )
            {
                dated++;
            }
        }

        if ( dated != 0 && dated != values.size() )
        {
            throw new DslRenderException( "'IN' cannot mix date-typed and other values: " + expr );
        }

        final List<Object> rendered = new ArrayList<>( values.size() );
        for ( final ValueExpr value : values )
        {
            rendered.add( scalar( value.getValue() ) );
        }

        final Map<String, Object> in = new LinkedHashMap<>();
        in.put( "field", fieldName( expr ) );
        if ( dated != 0 )
        {
            in.put( "type", "dateTime" );
        }
        in.put( "values", rendered );
        return wrap( "in", in );
    }

    private static Map<String, Object> renderFunction( final FunctionExpr function )
    {
        final String name = function.getName();
        final List<ValueExpr> args = function.getArguments();

        switch ( name )
        {
            case "fulltext":
                return renderSimpleQueryString( "fulltext", args, false );
            case "ngram":
                return renderSimpleQueryString( "ngram", args, false );
            case "stemmed":
                return renderSimpleQueryString( "stemmed", args, true );
            case "range":
                return renderRangeFunction( args );
            case "pathMatch":
                return renderPathMatch( args );
            default:
                throw new DslRenderException( "Function '" + name + "' is not supported" );
        }
    }

    private static Map<String, Object> renderSimpleQueryString( final String name, final List<ValueExpr> args, final boolean stemmed )
    {
        if ( args.size() < 2 || args.size() > 4 )
        {
            throw new DslRenderException(
                "Wrong number of arguments (" + args.size() + ") for function '" + name + "' (expected 2 to 4)" );
        }

        final String searchString = args.get( 1 ).getValue().asString();

        if ( !stemmed && "fulltext".equals( name ) && isNullOrEmpty( searchString ) )
        {
            return wrap( "matchAll", new LinkedHashMap<>() );
        }

        final Map<String, Object> expression = new LinkedHashMap<>();
        expression.put( "fields", weightedFields( args.get( 0 ).getValue().asString() ) );
        expression.put( "query", searchString );
        expression.put( "operator", operator( args ) );

        if ( stemmed )
        {
            expression.put( "language", args.size() > 3 ? args.get( 3 ).getValue().asString() : "" );
        }
        else if ( args.size() > 3 )
        {
            // gap G-3: the custom analyzer has no form in the public DSL, so it rides on the
            // wire schema's superset as an optional field the backend resolves.
            expression.put( "analyzer", args.get( 3 ).getValue().asString() );
        }

        return wrap( name, expression );
    }

    private static String operator( final List<ValueExpr> args )
    {
        if ( args.size() < 3 )
        {
            return "OR";
        }
        return args.get( 2 ).getValue().asString().toUpperCase();
    }

    private static List<Object> weightedFields( final String fields )
    {
        final List<Object> result = new ArrayList<>();
        for ( final String entry : fields.split( "," ) )
        {
            final int caret = entry.indexOf( '^' );
            if ( caret < 0 )
            {
                result.add( IndexPath.from( entry ).getPath() );
            }
            else
            {
                result.add( IndexPath.from( entry.substring( 0, caret ) ).getPath() + entry.substring( caret ) );
            }
        }
        return result;
    }

    /**
     * Reproduces {@code RangeFunctionArgsFactory}'s bound sniffing verbatim — including "a
     * plain string that ISO-offset-parses counts as an instant" — rather than leaving the
     * backend to re-sniff. Both bounds empty rewrites to {@code exists} (gap G-5).
     */
    private static Map<String, Object> renderRangeFunction( final List<ValueExpr> args )
    {
        if ( args.size() < 3 )
        {
            throw new DslRenderException( "Needs at least 3 arguments for range-function, got: [" + args.size() + "]" );
        }

        final String field = IndexPath.from( args.get( 0 ).getValue().asString() ).getPath();
        final Value from = args.get( 1 ).getValue();
        final Value to = args.get( 2 ).getValue();

        final boolean hasFrom = !isNullOrEmpty( from.asString() );
        final boolean hasTo = !isNullOrEmpty( to.asString() );

        if ( !hasFrom && !hasTo )
        {
            final Map<String, Object> exists = new LinkedHashMap<>();
            exists.put( "field", field );
            return wrap( "exists", exists );
        }

        final boolean includeFrom = args.size() >= 4 && args.get( 3 ).getValue().asBoolean();
        final boolean includeTo = args.size() >= 5 && args.get( 4 ).getValue().asBoolean();

        final boolean instant = from.isDateType() || isInstantIsoString( from );
        final boolean numeric = !instant && from.isNumericType();

        final Map<String, Object> range = new LinkedHashMap<>();
        range.put( "field", field );
        if ( instant )
        {
            range.put( "type", "dateTime" );
        }
        if ( hasFrom )
        {
            range.put( includeFrom ? "gte" : "gt", rangeBound( from, instant, numeric ) );
        }
        if ( hasTo )
        {
            range.put( includeTo ? "lte" : "lt", rangeBound( to, instant, numeric ) );
        }
        return wrap( "range", range );
    }

    private static Object rangeBound( final Value value, final boolean instant, final boolean numeric )
    {
        if ( instant )
        {
            return value.asInstant().toString();
        }
        if ( numeric )
        {
            return value.asDouble();
        }
        return value.asString();
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

    private static Map<String, Object> renderPathMatch( final List<ValueExpr> args )
    {
        if ( args.size() < 2 || args.size() > 3 )
        {
            throw new DslRenderException(
                "Wrong number of arguments (" + args.size() + ") for function 'pathMatch' (expected 2 to 3)" );
        }

        final Map<String, Object> pathMatch = new LinkedHashMap<>();
        pathMatch.put( "field", IndexPath.from( args.get( 0 ).getValue().asString() ).getPath() );
        pathMatch.put( "path", args.get( 1 ).getValue().asString() );
        if ( args.size() > 2 )
        {
            pathMatch.put( "minimumMatch", (double) args.get( 2 ).getValue().asDouble().intValue() );
        }
        return wrap( "pathMatch", pathMatch );
    }

    private static Map<String, Object> renderDsl( final DslExpr expr )
    {
        final Map<String, Object> rendered = toMap( expr.getExpression().getRoot() );
        if ( rendered.size() != 1 )
        {
            throw new DslRenderException( "Query allows only single root expression, but actual size is: " + rendered.size() );
        }
        return rendered;
    }

    private static Map<String, Object> toMap( final PropertySet set )
    {
        final Map<String, Object> map = new LinkedHashMap<>();
        for ( final PropertyArray array : set.getPropertyArrays() )
        {
            final List<Object> values = new ArrayList<>();
            for ( final Property property : array.getProperties() )
            {
                values.add( fromProperty( property.getValue() ) );
            }
            map.put( array.getName(), values.size() == 1 ? values.get( 0 ) : values );
        }
        return map;
    }

    private static Object fromProperty( final Value value )
    {
        if ( value.isPropertySet() )
        {
            return toMap( value.asData() );
        }
        if ( value.isNumericType() )
        {
            return value.asDouble();
        }
        if ( value.isBoolean() )
        {
            return value.asBoolean();
        }
        return value.asString();
    }

    /**
     * Numerics as JSON numbers with {@code Double} semantics; dated values as an ISO instant
     * carried by an explicit {@code type} (the DSL only reaches {@code ._datetime} when the
     * type is explicit); everything else stringified, which is where the AST puts it too.
     */
    private static Object scalar( final Value value )
    {
        rejectGeoPoint( value );

        if ( value.isDateType() )
        {
            return value.asInstant().toString();
        }
        if ( value.isNumericType() )
        {
            return value.asDouble();
        }
        return value.asString();
    }

    private static void rejectGeoPoint( final Value value )
    {
        if ( value.isGeoPoint() )
        {
            throw new DslRenderException(
                "geoPoint values are not supported in term/compare/range queries; use the geoDistance sort or a geo aggregation" );
        }
    }

    private static Value firstValue( final CompareExpr expr )
    {
        final ValueExpr firstValue = expr.getFirstValue();
        if ( firstValue == null )
        {
            throw new DslRenderException( "Invalid compare expression [" + expr + "]" );
        }
        return firstValue.getValue();
    }

    private static String fieldName( final CompareExpr expr )
    {
        return expr.getField().getIndexPath().getPath();
    }

    private static Map<String, Object> wrap( final String name, final Map<String, Object> expression )
    {
        final Map<String, Object> wrapper = new LinkedHashMap<>();
        wrapper.put( name, expression );
        return wrapper;
    }
}
