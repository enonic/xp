package com.enonic.xp.repo.impl.search.dsl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.enonic.xp.index.IndexPath;
import com.enonic.xp.query.expr.DslOrderExpr;
import com.enonic.xp.query.expr.DynamicOrderExpr;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.FunctionExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.util.GeoPoint;

/**
 * Renders an {@code OrderExpr} into one canonical sort element. {@code direction} is always
 * explicit — an absent direction is the engine default {@code ASC}, and spelling it out is
 * what makes the sort array comparable byte for byte.
 */
final class OrderDslRenderer
{
    private static final String GEO_DISTANCE = "geoDistance";

    private OrderDslRenderer()
    {
    }

    static Map<String, Object> render( final OrderExpr orderExpr )
    {
        if ( orderExpr instanceof FieldOrderExpr )
        {
            return renderField( (FieldOrderExpr) orderExpr );
        }
        if ( orderExpr instanceof DynamicOrderExpr )
        {
            return renderDynamic( (DynamicOrderExpr) orderExpr );
        }
        if ( orderExpr instanceof DslOrderExpr )
        {
            return renderDsl( (DslOrderExpr) orderExpr );
        }
        throw new DslRenderException( "Not able to render order expression of type " + orderExpr.getClass() );
    }

    private static Map<String, Object> renderField( final FieldOrderExpr orderExpr )
    {
        final Map<String, Object> sort = new LinkedHashMap<>();
        sort.put( "field", orderExpr.getField().getIndexPath().getPath() );
        final Locale language = orderExpr.getLanguage();
        if ( language != null )
        {
            sort.put( "language", language.toLanguageTag() );
        }
        sort.put( "direction", direction( orderExpr ) );
        return sort;
    }

    private static Map<String, Object> renderDynamic( final DynamicOrderExpr orderExpr )
    {
        final FunctionExpr function = orderExpr.getFunction();
        if ( !GEO_DISTANCE.equals( function.getName() ) )
        {
            throw new DslRenderException( "Not valid sort function: '" + function.getName() + "'" );
        }

        final List<ValueExpr> args = function.getArguments();
        if ( args.size() < 2 || args.size() > 3 )
        {
            throw new DslRenderException(
                "Wrong number of arguments (" + args.size() + ") for function 'geoDistance' (expected 2 to 3)" );
        }

        final GeoPoint geoPoint = args.get( 1 ).getValue().asGeoPoint();

        final Map<String, Object> location = new LinkedHashMap<>();
        location.put( "lat", geoPoint.getLatitude() );
        location.put( "lon", geoPoint.getLongitude() );

        final Map<String, Object> sort = new LinkedHashMap<>();
        sort.put( "field", IndexPath.from( args.get( 0 ).getValue().asString() ).getPath() );
        sort.put( "type", GEO_DISTANCE );
        sort.put( "location", location );
        if ( args.size() > 2 )
        {
            sort.put( "unit", args.get( 2 ).getValue().asString() );
        }
        sort.put( "direction", direction( orderExpr ) );
        return sort;
    }

    private static Map<String, Object> renderDsl( final DslOrderExpr orderExpr )
    {
        final Map<String, Object> sort = new LinkedHashMap<>();
        sort.put( "field", IndexPath.from( orderExpr.getField() ).getPath() );
        if ( orderExpr.getType() != null )
        {
            sort.put( "type", orderExpr.getType() );
        }
        if ( orderExpr.getLat() != null )
        {
            final Map<String, Object> location = new LinkedHashMap<>();
            location.put( "lat", orderExpr.getLat() );
            location.put( "lon", orderExpr.getLon() );
            sort.put( "location", location );
        }
        if ( orderExpr.getUnit() != null )
        {
            sort.put( "unit", orderExpr.getUnit() );
        }
        if ( orderExpr.getLanguage() != null )
        {
            sort.put( "language", orderExpr.getLanguage().toLanguageTag() );
        }
        sort.put( "direction", direction( orderExpr ) );
        return sort;
    }

    private static String direction( final OrderExpr orderExpr )
    {
        return ( orderExpr.getDirection() == null ? OrderExpr.Direction.ASC : orderExpr.getDirection() ).name();
    }
}
