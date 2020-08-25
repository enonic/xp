package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.function;

import java.util.List;

import com.enonic.xp.data.Value;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.util.GeoPoint;

public class GeoDistanceSortFunctionArguments
    extends AbstractFunctionArguments
{

    private static final int FIELD_POSITION = 0;

    private static final int LOCATION_POSITION = 1;

    private static final int UNIT_POSITION = 2;

    private static final int MIN_ARGUMENTS = 2;

    private static final int MAX_ARGUMENTS = 3;

    private double latitude;

    private double longitude;

    private final String fieldName;

    private String unit;

    public GeoDistanceSortFunctionArguments( final List<ValueExpr> arguments )
    {
        verifyNumberOfArguments( arguments );

        this.fieldName = arguments.get( FIELD_POSITION ).getValue().toString();

        setLocation( arguments );
        setUnit( arguments );
    }

    private void setLocation( final List<ValueExpr> arguments )
    {
        final Value locationArgument = arguments.get( LOCATION_POSITION ).getValue();

        try
        {
            final GeoPoint geoPoint = locationArgument.asGeoPoint();
            this.latitude = geoPoint.getLatitude();
            this.longitude = geoPoint.getLongitude();
        }
        catch ( Exception e )
        {
            throw new FunctionQueryBuilderException( "geoDistance", LOCATION_POSITION + 1, locationArgument.toString(), e );
        }
    }

    private void setUnit( final List<ValueExpr> arguments )
    {
        if ( arguments.size() == 3 )
        {
            final Value unitArgument = arguments.get( UNIT_POSITION ).getValue();

            this.unit = unitArgument.asString();
        }
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public String getUnit()
    {
        return unit;
    }

    @Override
    protected int getMinArguments()
    {
        return MIN_ARGUMENTS;
    }

    @Override
    protected int getMaxArguments()
    {
        return MAX_ARGUMENTS;
    }

    @Override
    public String getFunctionName()
    {
        return "geoDistance";
    }
}
