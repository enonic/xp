package com.enonic.wem.portal.postprocess;


import java.lang.reflect.Method;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.StandardELContext;
import javax.el.ValueExpression;

import com.google.common.collect.Maps;

public final class JavaElExpressionExecutor
    implements ExpressionExecutor
{
    private static ExpressionFactory factory = ExpressionFactory.newInstance();

    private Map<String, ValueExpression> cache = Maps.newHashMap();

    private final boolean cacheEnabled;

    public JavaElExpressionExecutor( final boolean cacheEnabled )
    {
        this.cacheEnabled = cacheEnabled;
    }

    @Override
    public String evaluateExpression( final String expression )
        throws Exception
    {
        final ELContext context = new StandardELContext( factory );

        if ( cacheEnabled )
        {
            final ValueExpression cachedVex = cache.get( expression );
            if ( cachedVex != null )
            {
                return (String) cachedVex.getValue( context );
            }
        }

        final Method createUrlMethod = JavaElExpressionExecutor.class.getDeclaredMethod( "createUrlElFunction", String.class );
        context.getFunctionMapper().mapFunction( "portal", "createUrl", createUrlMethod );
        final String expr = expression.startsWith( "${" ) ? expression : "${" + expression + "}";
        ValueExpression vex = factory.createValueExpression( context, expr, String.class );
        if ( cacheEnabled )
        {
            cache.put( expression, vex );
        }
        String result = (String) vex.getValue( context );

        return result;
    }

    public static String createUrlElFunction( final String value )
    {
        return "http://localhost/" + value;
    }
}
