package com.enonic.wem.xslt.internal.function;

import com.enonic.wem.portal.view.ViewFunctions;

final class PageUrlFunction
    extends AbstractUrlFunction
{
    public PageUrlFunction( final ViewFunctions functions )
    {
        super( "pageUrl", functions );
    }

    @Override
    protected String execute( final String... params )
    {
        return this.functions.pageUrl( params );
    }
}
