package com.enonic.xp.core.impl.image.parser;

public class CommandArgumentParser
{
    private CommandArgumentParser()
    {
    }

    private static Object getArg( Object[] args, int index )
    {
        if ( index < args.length )
        {
            return args[index];
        }

        return null;
    }

    public static int getIntArg( Object[] args, int index, int defValue )
    {
        return convertToInteger( getArg( args, index ), defValue );
    }

    public static String getStringArg( Object[] args, int index, String defValue )
    {
        return convertToString( getArg( args, index ), defValue );
    }

    public static float getFloatArg( Object[] args, int index, float defValue )
    {
        return convertToFloat( getArg( args, index ), defValue );
    }

    public static double getDoubleArg( Object[] args, int index, double defValue )
    {
        return convertToDouble( getArg( args, index ), defValue );
    }

    public static boolean getBooleanArg( Object[] args, int index, boolean defValue )
    {
        return convertToBoolean( getArg( args, index ), defValue );
    }

    private static String convertToString( Object arg, String defValue )
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

    private static Integer convertToInteger( Object arg, Integer defValue )
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
            return Integer.valueOf( arg.toString() );
        }
        catch ( Exception e )
        {
            return defValue;
        }
    }

    private static Float convertToFloat( Object arg, Float defValue )
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
            return Float.valueOf( arg.toString() );
        }
        catch ( Exception e )
        {
            return defValue;
        }
    }

    private static Double convertToDouble( Object arg, Double defValue )
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
            return Double.valueOf( arg.toString() );
        }
        catch ( Exception e )
        {
            return defValue;
        }
    }

    private static Boolean convertToBoolean( Object arg, Boolean defValue )
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
