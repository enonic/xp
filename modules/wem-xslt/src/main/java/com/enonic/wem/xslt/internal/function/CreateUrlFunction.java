package com.enonic.wem.xslt.internal.function;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.SequenceType;

final class CreateUrlFunction
    extends AbstractFunction
{
    private final class Call
        extends AbstractFunctionCall
    {
        @Override
        public Sequence call( final XPathContext context, final Sequence[] arguments )
            throws XPathException
        {
            final String local = toSingleString( arguments[0] );
            String[] params = new String[0];

            if ( arguments.length > 1 )
            {
                params = toStringArray( arguments[1] );
            }

            final String result = PortalUrlFunctions.get().createUrl( local, params );
            return createValue( result );
        }
    }

    public CreateUrlFunction()
    {
        super( "createUrl" );
        setMinimumNumberOfArguments( 1 );
        setMaximumNumberOfArguments( 2 );
        setResultType( SequenceType.SINGLE_STRING );
    }

    @Override
    public ExtensionFunctionCall makeCallExpression()
    {
        return new Call();
    }
}
