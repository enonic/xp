package com.enonic.xp.portal.impl.macro;

import java.util.regex.Pattern;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.macro.MacroContext;
import com.enonic.xp.portal.macro.MacroProcessor;
import com.enonic.xp.portal.postprocess.HtmlTag;

public class YoutubeMacroProcessor
    implements MacroProcessor
{

    private static final String URL_PARAM = "url";

    private static final String YOUTUBE_EMBED_URL = "https://www.youtube.com/embed/";

    private static final String COMMON_STYLE_FILE_PATH = "/css/macro/youtube/youtube.css";

    private static final Pattern YOUTUBE_URL_PATTERN =
        Pattern.compile( "^(https://|http://)?(www\\.)?(m\\.)?(youtu\\.be/|youtube\\.com/)(.)+" );

    @Override
    public PortalResponse process( final MacroContext context )
    {
        if ( !hasAttr( context, URL_PARAM ) || !isYoutubeUrl( context.getParam( URL_PARAM ) ) )
        {
            return null;
        }

        final PortalResponse.Builder response = PortalResponse.create();
        addBody( response, context );
        addCommonStyleContribution( response, context );
        return response.build();
    }

    private void addBody( final PortalResponse.Builder portalResponse, final MacroContext context )
    {

        final String html =
            "<div class='youtube-video-wrapper'><iframe src='" + convertUrl( context.getParam( URL_PARAM ) ) + "'></iframe></div>";

        portalResponse.body( html );
    }

    private void addCommonStyleContribution( final PortalResponse.Builder portalResponse, final MacroContext context )
    {
        if ( context.getSystemAssetsBaseUri() != null )
        {
            portalResponse.contribution( HtmlTag.HEAD_BEGIN, this.makeCommonStyleContributionRef( context ) );
        }
    }

    private String makeCommonStyleContributionRef( final MacroContext context )
    {
        return "<link rel='stylesheet' type='text/css' href='" + context.getSystemAssetsBaseUri() + COMMON_STYLE_FILE_PATH +
            "'/>";
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
}
