package com.enonic.xp.core.impl.image.command;

public abstract class BaseImageCommand
{
    protected final String name;

    protected BaseImageCommand( String name) {
        this.name = name;
    }

    public abstract Object build( Object[] args );

    public final String getName()
    {
        return this.name;
    }

    private Object getArg( Object[] args, int index )
    {
        if ( index < args.length )
        {
            return args[index];
        }

        return null;
    }

    protected final int getIntArg( Object[] args, int index, int defValue )
    {
        return convertToInteger( getArg( args, index ), defValue );
    }

    protected final String getStringArg( Object[] args, int index, String defValue )
    {
        return convertToString( getArg( args, index ), defValue );
    }

    protected final float getFloatArg( Object[] args, int index, float defValue )
    {
        return convertToFloat( getArg( args, index ), defValue );
    }

    protected final double getDoubleArg( Object[] args, int index, double defValue )
    {
        return convertToDouble( getArg( args, index ), defValue );
    }

    protected final boolean getBooleanArg( Object[] args, int index, boolean defValue )
    {
        return convertToBoolean( getArg( args, index ), defValue );
    }

    private String convertToString( Object arg, String defValue )
    {
        if ( arg == null )
        {
            return defValue;
        }

        if ( arg instanceof String )
        {
            return (String) arg;
        }

        return arg.toString();
    }

    private Integer convertToInteger( Object arg, Integer defValue )
    {
        if ( arg == null )
        {
            return defValue;
        }

        if ( arg instanceof Number )
        {
            return ( (Number) arg ).intValue();
        }

        try
        {
            return new Integer( arg.toString() );
        }
        catch ( Exception e )
        {
            return defValue;
        }
    }

    private Float convertToFloat( Object arg, Float defValue )
    {
        if ( arg == null )
        {
            return defValue;
        }

        if ( arg instanceof Number )
        {
            return ( (Number) arg ).floatValue();
        }

        try
        {
            return new Float( arg.toString() );
        }
        catch ( Exception e )
        {
            return defValue;
        }
    }

    private Double convertToDouble( Object arg, Double defValue )
    {
        if ( arg == null )
        {
            return defValue;
        }

        if ( arg instanceof Number )
        {
            return ( (Number) arg ).doubleValue();
        }

        try
        {
            return new Double( arg.toString() );
        }
        catch ( Exception e )
        {
            return defValue;
        }
    }

    private Boolean convertToBoolean( Object arg, Boolean defValue )
    {
        if ( arg == null )
        {
            return defValue;
        }

        if ( arg instanceof Boolean )
        {
            return (Boolean) arg;
        }

        try
        {
            return Boolean.parseBoolean( arg.toString() );
        }
        catch ( Exception e )
        {
            return defValue;
        }
    }
}
