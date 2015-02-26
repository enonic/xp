package com.enonic.xp.portal.impl.xslt.function;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.SequenceType;

import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.PortalContextAccessor;
import com.enonic.xp.portal.view.ViewFunctionParams;
import com.enonic.xp.portal.view.ViewFunctionService;

final class XsltViewFunction
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

            final String result = execute( params );
            return createValue( result );
        }
    }

    private String name;

    protected ViewFunctionService service;

    public XsltViewFunction( final String name )
    {
        super( name );
        this.name = name;
        setMinimumNumberOfArguments( 0 );
        setMaximumNumberOfArguments( 100 );
        setResultType( SequenceType.SINGLE_STRING );
        setArgumentTypes( SequenceType.SINGLE_STRING );
    }

    @Override
    public ExtensionFunctionCall makeCallExpression()
    {
        return new Call();
    }

    private PortalContext getContext()
    {
        return PortalContextAccessor.get();
    }

    private String execute( final String[] args )
    {
        final ViewFunctionParams params = new ViewFunctionParams().context( getContext() ).name( this.name ).args( args );
        return this.service.execute( params ).toString();
    }
}
