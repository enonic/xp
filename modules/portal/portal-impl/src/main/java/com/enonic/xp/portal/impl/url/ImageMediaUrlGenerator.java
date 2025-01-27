package com.enonic.xp.portal.impl.url;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.url.ImageMediaUrlParams;

public class ImageMediaUrlGenerator
    extends BaseUrlGenerator<ImageMediaUrlParams>
{
    private final ImageMediaHarmonizedUrlStrategy imageMediaHarmonizedUrlStrategy;

    private final ImageMediaSlashApiUrlStrategy imageMediaSlashApiUrlStrategy;

    public ImageMediaUrlGenerator( final ContentService contentService )
    {
        super( contentService );

        this.imageMediaHarmonizedUrlStrategy = new ImageMediaHarmonizedUrlStrategy( contentService );
        this.imageMediaSlashApiUrlStrategy = new ImageMediaSlashApiUrlStrategy( contentService );
    }

    @Override
    public String doGenerateUrl( final ImageMediaUrlParams params )
    {
        return resolveStrategy( params ).generateUrl( params );
    }

    private MediaUrlStrategy resolveStrategy( final ImageMediaUrlParams params )
    {
        return params.getWebRequest() instanceof PortalRequest ? imageMediaHarmonizedUrlStrategy : imageMediaSlashApiUrlStrategy;
    }
}
