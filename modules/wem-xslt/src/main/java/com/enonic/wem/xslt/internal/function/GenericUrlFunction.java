package com.enonic.wem.xslt.internal.function;

import com.enonic.wem.portal.view.ViewFunctions;

final class GenericUrlFunction
    extends AbstractUrlFunction
{
    public GenericUrlFunction( final ViewFunctions functions )
    {
        super( "url", functions );
    }

    @Override
    protected String execute( final String... params )
    {
        return this.functions.url( params );
    }
}
