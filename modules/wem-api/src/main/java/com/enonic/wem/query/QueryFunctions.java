/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.wem.query;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.ReadableDateTime;

import com.enonic.wem.query.expr.ArrayExpr;
import com.enonic.wem.query.expr.FunctionExpr;
import com.enonic.wem.query.expr.ValueExpr;

/**
 * This class defines the query functions.
 */
public final class QueryFunctions
{

    private static Object executeMethod( String name, Object[] args )
        throws QueryParserException
    {
        Class[] types = new Class[args.length];
        for ( int i = 0; i < args.length; i++ )
        {
            types[i] = args[i].getClass();
        }

        Method method;

        try
        {
            method = findMethod( name, types );
        }
        catch ( Exception e )
        {
            throw new QueryParserException( "Function " + name + "() not found" );
        }

        try
        {
            return method.invoke( null, args );
        }
        catch ( InvocationTargetException e )
        {
            Throwable ex = e.getTargetException();

            if ( ex instanceof QueryParserException )
            {
                throw (QueryParserException) ex;
            }
            else
            {
                throw new QueryParserException( "Error invoking " + name + "(): " + ex.getMessage() );
            }
        }
        catch ( Exception e )
        {
            throw new QueryParserException( "Failed to invoke " + name + "()" );
        }
    }

    public static ValueExpr executeFunction( FunctionExpr expr )
        throws QueryParserException
    {
        String name = expr.getName();
        ArrayExpr args = expr.getArguments();
        return executeFunction( name, args.getValue() );
    }

    private static ValueExpr executeFunction( String name, ValueExpr[] args )
        throws QueryParserException
    {
        Object[] values = new Object[args.length];
        for ( int i = 0; i < args.length; i++ )
        {
            values[i] = args[i].getValue();
        }

        Object ret = executeMethod( name, values );
        if ( ret instanceof Number )
        {
            return ValueExpr.number( "" + ret );
        }
        else if ( ret instanceof ReadableDateTime )
        {
            return ValueExpr.date( "" + ret );
        }
        else if ( ret != null )
        {
            return ValueExpr.string( ret.toString() );
        }
        else
        {
            return ValueExpr.string( "0" );
        }
    }

    private static Method findMethod( String name, Class[] types )
        throws Exception
    {
        String prefix = null;
        String local = name;

        int pos = name.indexOf( ':' );
        if ( pos > 0 )
        {
            prefix = name.substring( 0, pos );
            local = name.substring( pos + 1 );
        }
        else if ( pos == 0 )
        {
            local = name.substring( 1 );
        }

        return findMethod( prefix, local, types );
    }

    private static Method findMethod( String prefix, String local, Class[] types )
        throws Exception
    {
        if ( prefix == null )
        {
            return findInternalMethod( local, types );
        }
        else
        {
            return findExternalMethod( prefix, local, types );
        }
    }

    private static Method findInternalMethod( String name, Class[] types )
        throws Exception
    {
        return findMethod( QueryFunctions.class, name, types );
    }

    private static Method findExternalMethod( String prefix, String local, Class[] types )
        throws Exception
    {
        Class clz = Class.forName( prefix );
        return findMethod( clz, local, types );
    }

    private static Method findMethod( Class clz, String name, Class[] types )
        throws Exception
    {
        return clz.getMethod( name, types );
    }

    /**
     * Built in now() function.
     *
     * @return A <code>Date</code> object that holds the current date and time.
     */
    public static DateTime now()
    {
        return new DateTime();
    }

    /**
     * Built in today() function.
     *
     * @return A <code>Date</code> object that holds the current date and time.
     */
    public static DateMidnight today()
    {
        // We use DateMidnight to later recognise that user have not specified time
        return new DateMidnight();
    }

    public static DateMidnight todayOffset( Double offset )
    {
        // We use DateMidnight to later recognise that user have not specified time
        int offsetInDays = offset.intValue();
        if ( offsetInDays >= 0 )
        {
            return new DateMidnight().plusDays( offsetInDays );
        }
        else
        {
            return new DateMidnight().minusDays( offsetInDays * ( -1 ) );
        }
    }

    /**
     * Built in date() function.
     *
     * @param value The date to parse.
     * @return A <code>Date</code> object that holds the date, parsed from the input <code>value</code>.
     */
    public static ReadableDateTime date( String value )
    {
        return new DateTime(value);
    }
}
