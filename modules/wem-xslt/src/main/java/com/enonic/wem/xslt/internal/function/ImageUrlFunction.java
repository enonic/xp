package com.enonic.wem.xslt.internal.function;

import com.enonic.wem.portal.view.ViewFunctions;

final class ImageUrlFunction
    extends AbstractUrlFunction
{
    public ImageUrlFunction( final ViewFunctions functions )
    {
        super( "imageUrl", functions );
    }

    @Override
    protected String execute( final String... params )
    {
        return this.functions.imageUrl( params );
    }
}
