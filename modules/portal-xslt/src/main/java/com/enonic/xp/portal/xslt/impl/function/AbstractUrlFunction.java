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
import com.enonic.xp.portal.url.PortalUrlService;

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

    protected PortalUrlService urlService;

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

    protected final PortalContext getContext()
    {
        return PortalContextAccessor.get();
    }

    protected final PortalUrlBuilders createUrlBuilders()
    {
        return new PortalUrlBuilders( getContext() );
    }

    protected abstract String execute( final Multimap<String, String> map );
}
