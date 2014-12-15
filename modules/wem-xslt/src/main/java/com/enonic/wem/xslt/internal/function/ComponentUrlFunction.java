package com.enonic.wem.xslt.internal.function;

import com.google.common.collect.Multimap;

import com.enonic.wem.portal.view.ViewFunctions;

final class ComponentUrlFunction
    extends AbstractUrlFunction
{
    public ComponentUrlFunction( final ViewFunctions functions )
    {
        super( "componentUrl", functions );
    }

    @Override
    protected String execute( final Multimap<String, String> params )
    {
        return this.functions.componentUrl( params );
    }
}
