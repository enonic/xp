package com.enonic.wem.core.index.query.function;

import java.util.List;

import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.data.type.GeoPoint;
import com.enonic.wem.api.query.expr.ValueExpr;

public class GeoDistanceSortFunctionArguments
    extends AbstractFunctionArguments
{

    public static final int FIELD_POSITION = 0;

    public static final int LOCATION_POSITION = 1;

    private static int MIN_ARGUMENTS = 2;

    private static int MAX_ARGUMENTS = 2;

    private double latitude;

    private double longitude;

    private String fieldName;


    public GeoDistanceSortFunctionArguments( final List<ValueExpr> arguments )
    {
        verifyNumberOfArguments( arguments );

        this.fieldName = arguments.get( FIELD_POSITION ).getValue().toString();

        setLocation( arguments );
    }

    private void setLocation( final List<ValueExpr> arguments )
    {
        final Value<?> locationArgument = arguments.get( LOCATION_POSITION ).getValue();

        try
        {
            new Value.GeoPoint( locationArgument.toString() );

            this.latitude = GeoPoint.getLatitude( locationArgument.asString() );
            this.longitude = GeoPoint.getLongitude( locationArgument.asString() );
        }
        catch ( Exception e )
        {
            throw new FunctionQueryBuilderException( "geoDistance", LOCATION_POSITION + 1, locationArgument.toString(), e );
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

    @Override
    int getMinArguments()
    {
        return MIN_ARGUMENTS;
    }

    @Override
    int getMaxArguments()
    {
        return MAX_ARGUMENTS;
    }

    @Override
    public String getFunctionName()
    {
        return "geoDistance";
    }
}
