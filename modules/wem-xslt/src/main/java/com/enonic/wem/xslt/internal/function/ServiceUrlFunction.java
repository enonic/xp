package com.enonic.wem.xslt.internal.function;

import com.enonic.wem.portal.view.ViewFunctions;

final class ServiceUrlFunction
    extends AbstractUrlFunction
{
    public ServiceUrlFunction( final ViewFunctions functions )
    {
        super( "serviceUrl", functions );
    }

    @Override
    protected String execute( final String... params )
    {
        return this.functions.serviceUrl( params );
    }
}
