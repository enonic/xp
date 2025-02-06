package com.enonic.xp.portal.impl.url;

import com.enonic.xp.portal.url.BaseUrlStrategy;
import com.enonic.xp.portal.url.ImageUrlGeneratorParams;
import com.enonic.xp.portal.url.PathPrefixStrategy;
import com.enonic.xp.portal.url.RewritePathStrategy;

public class UrlService
{
    public String imageUrl( ImageUrlGeneratorParams params )
    {
        final BaseUrlStrategy baseUrlStrategy = params.getBaseUrlStrategy();
        final PathPrefixStrategy pathPrefixStrategy = params.getPathPrefixStrategy();
        final RewritePathStrategy rewritePathStrategy = params.getRewritePathStrategy();

        final ImageMediaPathStrategyParams imageMediaPathStrategyParams = ImageMediaPathStrategyParams.create()
            .setMedia( params.getMedia() )
            .setProjectName( params.getProjectName() )
            .setBranch( params.getBranch() )
            .setScale( params.getScale() )
            .build();

        final MediaPathStrategy mediaPathStrategy =
            new MediaPathStrategy( pathPrefixStrategy, new ImageMediaPathStrategy( imageMediaPathStrategyParams ) );

        return UrlGenerator.generateUrl( baseUrlStrategy, mediaPathStrategy, rewritePathStrategy );
    }

}
