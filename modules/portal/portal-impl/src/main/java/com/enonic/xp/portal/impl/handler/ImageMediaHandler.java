package com.enonic.xp.portal.impl.handler;

import java.util.Objects;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.image.ImageService;
import com.enonic.xp.image.ScaleParamsParser;
import com.enonic.xp.media.MediaInfoService;
import com.enonic.xp.portal.impl.PortalConfig;
import com.enonic.xp.portal.impl.handler.image.ImageHandlerWorker;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

@Component(service = UniversalApiHandler.class, property = {"applicationKey=media", "apiKey=image", "displayName=Image Media API",
    "allowedPrincipals=role:system.everyone", "mount=true"}, configurationPid = "com.enonic.xp.portal")
public class ImageMediaHandler
    extends MediaHandlerBase
{
    private final ImageService imageService;

    private final MediaInfoService mediaInfoService;

    @Activate
    public ImageMediaHandler( @Reference final ContentService contentService, @Reference final ImageService imageService,
                              @Reference final MediaInfoService mediaInfoService )
    {
        super( contentService );
        this.imageService = imageService;
        this.mediaInfoService = mediaInfoService;
    }

    @Activate
    @Modified
    public void activate( final PortalConfig config )
    {
        doActivate( config );
    }

    @Override
    public WebResponse handle( final WebRequest webRequest )
    {
        final String path = Objects.requireNonNullElse( webRequest.getEndpointPath(), webRequest.getRawPath() );
        final ImagePathParser pathParser = new ImagePathParser( path );
        final ImagePathMetadata pathMetadata = pathParser.parse();

        if ( webRequest.getMethod() == HttpMethod.OPTIONS )
        {
            return HandlerHelper.handleDefaultOptions( ALLOWED_METHODS );
        }

        checkArguments( webRequest, pathMetadata );

        return ContextBuilder.copyOf( ContextAccessor.current() )
            .repositoryId( pathMetadata.repositoryId )
            .branch( pathMetadata.branch )
            .build()
            .callWith( () -> {
                final ImageHandlerWorker worker =
                    new ImageHandlerWorker( webRequest, this.contentService, this.imageService, this.mediaInfoService );

                worker.id = pathMetadata.contentId;
                worker.fingerprint = pathMetadata.fingerprint;
                worker.scaleParams = new ScaleParamsParser().parse( pathMetadata.scaleParams );
                worker.name = pathMetadata.name;
                worker.filterParam = HandlerHelper.getParameter( webRequest, "filter" );
                worker.qualityParam = HandlerHelper.getParameter( webRequest, "quality" );
                worker.backgroundParam = HandlerHelper.getParameter( webRequest, "background" );
                worker.privateCacheControlHeaderConfig = this.privateCacheControlHeaderConfig;
                worker.publicCacheControlHeaderConfig = this.publicCacheControlHeaderConfig;
                worker.contentSecurityPolicy = this.contentSecurityPolicy;
                worker.contentSecurityPolicySvg = this.contentSecurityPolicySvg;
                worker.branch = pathMetadata.branch;

                return worker.execute();
            } );
    }

    private static final class ImagePathMetadata
        extends MediaHandlerBase.PathMetadata
    {
        String scaleParams;
    }

    private static final class ImagePathParser
        extends MediaHandlerBase.PathParser<ImagePathMetadata>
    {
        // Image path is: "{project[:draft]}/{id[:fingerprint]}/{scaleFn}/{name}"

        static final String FRAMED_API_HANDLER_KEY = "/media:image/";

        static final int PATH_VARIABLES_LIMIT = 5;

        static final int SCALE_INDEX = 2;

        static final int NAME_INDEX = 3;

        ImagePathParser( final String path )
        {
            super( path, FRAMED_API_HANDLER_KEY, PATH_VARIABLES_LIMIT );
        }

        ImagePathMetadata parse()
        {
            final ImagePathMetadata metadata = doParse();

            metadata.scaleParams = pathVariables[SCALE_INDEX];
            metadata.name = pathVariables[NAME_INDEX];

            return metadata;
        }

        @Override
        ImagePathMetadata createMetadata()
        {
            return new ImagePathMetadata();
        }
    }
}
