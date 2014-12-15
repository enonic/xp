package com.enonic.xp.portal.xslt.impl.function;

import com.google.common.collect.Multimap;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.SequenceType;

import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.PortalContextAccessor;
import com.enonic.xp.portal.url.PortalUrlBuilders;
import com.enonic.xp.portal.url.PortalUrlBuildersHelper;

abstract class AbstractUrlFunction
    extends AbstractFunction
{
    private final class Call
        extends AbstractFunctionCall
    {
        @Override
        public Sequence call( final XPathContext context, final Sequence[] arguments )
            throws XPathException
        {
            final String[] params = new String[arguments.length];
            for ( int i = 0; i < arguments.length; i++ )
            {
                params[i] = toSingleString( arguments[i] );
            }

            final Multimap<String, String> paramsMap = PortalUrlBuildersHelper.toParamMap( params );
            final String result = execute( paramsMap );
            return createValue( result );
        }
    }

    public AbstractUrlFunction( final String name )
    {
        super( name );
        setMinimumNumberOfArguments( 0 );
        setMaximumNumberOfArguments( 100 );
        setResultType( SequenceType.SINGLE_STRING );
        setArgumentTypes( SequenceType.SINGLE_STRING );
    }

    @Override
    public final ExtensionFunctionCall makeCallExpression()
    {
        return new Call();
    }

    protected final PortalUrlBuilders createUrlBuilders()
    {
        final PortalContext context = PortalContextAccessor.get();
        return new PortalUrlBuilders( context );
    }

    protected abstract String execute( final Multimap<String, String> params );
}
