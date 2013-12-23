package com.enonic.wem.portal.postprocess;


import java.lang.reflect.Method;
import java.util.Map;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import com.google.common.collect.Maps;

import de.odysseus.el.ExpressionFactoryImpl;
import de.odysseus.el.util.SimpleContext;

public final class JavaElExpressionExecutor
    implements ExpressionExecutor
{
    private static ExpressionFactory factory = new ExpressionFactoryImpl();

    private Map<String, ValueExpression> cache = Maps.newHashMap();

    private final boolean cacheEnabled;

    private final SimpleContext context;

    public JavaElExpressionExecutor( final boolean cacheEnabled )
        throws Exception
    {
        this.cacheEnabled = cacheEnabled;
        this.context = new SimpleContext();
        final Method createUrlMethod = JavaElExpressionExecutor.class.getDeclaredMethod( "createUrlElFunction", String.class );
        this.context.setFunction( "portal", "createUrl", createUrlMethod );
    }

    @Override
    public String evaluateExpression( final String expression )
        throws Exception
    {
        if ( cacheEnabled )
        {
            final ValueExpression cachedVex = cache.get( expression );
            if ( cachedVex != null )
            {
                return (String) cachedVex.getValue( context );
            }
        }

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
