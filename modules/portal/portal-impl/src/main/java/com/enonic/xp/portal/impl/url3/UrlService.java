package com.enonic.xp.portal.impl.url3;

import com.enonic.xp.portal.url.BaseUrlStrategy;
import com.enonic.xp.portal.url.PathPrefixStrategy;
import com.enonic.xp.portal.url.RewritePathStrategy;

public class UrlService
{
    public String imageUrl( com.enonic.xp.portal.url.ImageUrlGeneratorParams params )
    {
        final BaseUrlStrategy baseUrlStrategy = params.baseUrlStrategy;
        final PathPrefixStrategy pathPrefixStrategy = params.pathPrefixStrategy;
        final RewritePathStrategy rewritePathStrategy = params.rewritePathStrategy;

        final ImageMediaPathStrategyParams imageMediaPathStrategyParams = ImageMediaPathStrategyParams.create()
            .setMedia( params.mediaProvider.get() )
            .setProjectName( params.projectName )
            .setBranch( params.branch )
            .setScale( params.scale )
            .build();

        final MediaPathStrategy mediaPathStrategy =
            new MediaPathStrategy( pathPrefixStrategy, new ImageMediaPathStrategy( imageMediaPathStrategyParams ) );

        return UrlGenerator.generateUrl( baseUrlStrategy, mediaPathStrategy, rewritePathStrategy );
    }

    private ImageMediaPathStrategyParams map( final ImageUrlGeneratorParams source )
    {
        return ImageMediaPathStrategyParams.create()
            .setMedia( source.getMedia() )
            .setProjectName( source.getProjectName() )
            .setBranch( source.getBranch() )
            .setScale( source.getScale() )
            .build();
    }

}
