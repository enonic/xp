package com.enonic.wem.xslt.internal.function;

import com.google.common.collect.Multimap;

import com.enonic.wem.portal.view.ViewFunctions;

final class GeneralUrlFunction
    extends AbstractUrlFunction
{
    public GeneralUrlFunction( final ViewFunctions functions )
    {
        super( "url", functions );
    }

    @Override
    protected String execute( final Multimap<String, String> params )
    {
        return this.functions.url( params );
    }
}
