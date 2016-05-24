package com.enonic.xp.portal.impl.macro;

import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.macro.MacroContext;
import com.enonic.xp.portal.url.AssetUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;

@Component(immediate = true, service = BuiltInMacroProcessor.class)
public class YoutubeMacroProcessor
    implements BuiltInMacroProcessor
{

    private static final String URL_PARAM = "url";

    private static final String WIDTH_PARAM = "width";

    private static final String HEIGHT_PARAM = "height";

    private static final String YOUTUBE_EMBED_URL = "https://www.youtube.com/embed/";

    private static final Pattern YOUTUBE_URL_PATTERN =
        Pattern.compile( "^(https://|http://)?(www\\.)?(m\\.)?(youtu\\.be/|youtube\\.com/)(.)+" );

    static final String COMMON_STYLE_ASSET_PATH = "css/macro/youtube/youtube.css";

    static final String SYSTEM_APPLICATION_KEY = "com.enonic.xp.app.system";

    private PortalUrlService urlService;

    @Override
    public String getName()
    {
        return "youtube";
    }

    @Override
    public PortalResponse process( final MacroContext context )
    {
        if ( !hasAttr( context, URL_PARAM ) || !isYoutubeUrl( context.getParam( URL_PARAM ) ) )
        {
            return null;
        }

        final AssetUrlParams assetParams = new AssetUrlParams().portalRequest( context.getRequest() ).
            application( SYSTEM_APPLICATION_KEY ).
            path( COMMON_STYLE_ASSET_PATH );
        final String cssAssetUrl = urlService.assetUrl( assetParams );
        // TODO use in page contributions

        final String html =
            "<iframe src=\"" + convertUrl( context.getParam( URL_PARAM ) ) + "\"" + makeWidthAttr( context ) + makeHeightAttr( context ) +
                "></iframe>";

        return PortalResponse.create().body( html ).build();
    }

    private boolean isYoutubeUrl( final String url )
    {
        return YOUTUBE_URL_PATTERN.matcher( url ).find();
    }

    private boolean hasAttr( final MacroContext macroContext, final String name )
    {
        return macroContext.getParam( name ) != null;
    }

    private String convertUrl( final String url )
    {
        return YOUTUBE_EMBED_URL + fetchVideoId( url );
    }

    private String fetchVideoId( final String url )
    {
        final String pathWithVideoId = fetchLastPathOfUrl( url );

        if ( urlPathHasAttributes( pathWithVideoId ) )
        {
            return findVideoIdWithinPathAttributes( pathWithVideoId );
        }

        return pathWithVideoId;  // matches ...www.youtube.com/v/gdfgdfg and ...youtu.be/cFfxuWUgcvI
    }

    private String findVideoIdWithinPathAttributes( final String path )
    {
        final int indexOfIdKey = path.lastIndexOf( "v=" );
        if ( indexOfIdKey > -1 )
        {
            final String videoId = path.substring( indexOfIdKey + 2 );
            final int indexOfAmp = videoId.indexOf( "&" );
            if ( indexOfAmp > -1 )
            { // case like v=cFfxuWUgcvI&t=2
                return videoId.substring( 0, indexOfAmp );
            }
            else
            {
                return videoId;
            }
        }
        return path.substring( 0, path.indexOf( "?" ) ); // ...www.youtube.com/v/-aAbBcCdDeE?version=3&autohide=1
    }

    private String fetchLastPathOfUrl( final String url )
    {
        return url.substring( url.lastIndexOf( "/" ) + 1 );
    }

    private boolean urlPathHasAttributes( final String path )
    {
        return path.contains( "?" );
    }

    private String makeWidthAttr( final MacroContext macroContext )
    {
        final String result = macroContext.getParam( WIDTH_PARAM );
        return result != null ? " width=\"" + result + "\"" : "";
    }

    private String makeHeightAttr( final MacroContext macroContext )
    {
        final String result = macroContext.getParam( HEIGHT_PARAM );
        return result != null ? " height=\"" + result + "\"" : "";
    }

    @Reference
    public void setPortalUrlService( final PortalUrlService portalUrlService )
    {
        this.urlService = portalUrlService;
    }
}
