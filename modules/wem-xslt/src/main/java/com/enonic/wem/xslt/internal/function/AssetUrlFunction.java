package com.enonic.wem.xslt.internal.function;

import com.google.common.collect.Multimap;

import com.enonic.wem.portal.view.ViewFunctions;

final class AssetUrlFunction
    extends AbstractUrlFunction
{
    public AssetUrlFunction( final ViewFunctions functions )
    {
        super( "assetUrl", functions );
    }

    @Override
    protected String execute( final Multimap<String, String> params )
    {
        return this.functions.assetUrl( params );
    }
}
