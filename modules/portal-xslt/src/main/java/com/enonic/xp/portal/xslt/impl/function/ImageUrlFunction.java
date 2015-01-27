package com.enonic.xp.portal.xslt.impl.function;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.url.ImageUrlParams;

final class ImageUrlFunction
    extends AbstractUrlFunction
{
    public ImageUrlFunction()
    {
        super( "imageUrl" );
    }

    @Override
    protected String execute( final Multimap<String, String> map )
    {
        final ImageUrlParams params = new ImageUrlParams().context( getContext() ).setAsMap( map );
        return this.urlService.imageUrl( params );
    }
}
