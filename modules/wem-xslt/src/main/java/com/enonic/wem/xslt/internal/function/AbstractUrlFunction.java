package com.enonic.wem.xslt.internal.function;

import com.google.common.collect.Multimap;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.SequenceType;

import com.enonic.wem.portal.view.ViewFunctions;
import com.enonic.wem.portal.view.ViewHelper;

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

            final Multimap<String, String> paramsMap = ViewHelper.toParamMap( params );
            final String result = execute( paramsMap );
            return createValue( result );
        }
    }

    protected final ViewFunctions functions;

    public AbstractUrlFunction( final String name, final ViewFunctions functions )
    {
        super( name );
        this.functions = functions;
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

    protected abstract String execute( final Multimap<String, String> params );
}
