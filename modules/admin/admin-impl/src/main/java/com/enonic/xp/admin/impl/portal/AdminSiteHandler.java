package com.enonic.xp.admin.impl.portal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.handler.BaseSiteHandler;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.exception.ExceptionMapper;
import com.enonic.xp.web.exception.ExceptionRenderer;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;

import static com.google.common.base.Strings.nullToEmpty;

@Component(immediate = true, service = WebHandler.class, configurationPid = "com.enonic.xp.admin")
public class AdminSiteHandler
    extends BaseSiteHandler
{
    private static final String BASE_URI_START = "/admin/site";

    private static final Pattern BASE_URI_PATTERN = Pattern.compile( "^" + BASE_URI_START + "/(edit|preview|admin|inline)" );

    private volatile String previewContentSecurityPolicy;

    @Activate
    @Modified
    public void activate( final AdminConfig config )
    {
        previewContentSecurityPolicy = config.site_previewContentSecurityPolicy();
    }

    @Override
    protected boolean canHandle( final WebRequest webRequest )
    {
        return webRequest.getRawPath().startsWith( BASE_URI_START );
    }

    @Override
    protected PortalRequest createPortalRequest( final WebRequest webRequest, final WebResponse webResponse )
    {
        final Matcher matcher = BASE_URI_PATTERN.matcher( webRequest.getRawPath() );
        if ( !matcher.find() )
        {
            throw WebException.notFound( "Mode needs to be specified" );
        }
        final String baseUri = matcher.group( 0 );
        final RenderMode mode = RenderMode.from( matcher.group( 1 ) );
        final String baseSubPath = webRequest.getRawPath().substring( baseUri.length() + 1 );

        final PortalRequest portalRequest = doCreatePortalRequest( webRequest, baseUri, baseSubPath );
        portalRequest.setMode( mode );

        return portalRequest;
    }

    @Override
    protected WebResponse doHandle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
    {
        final WebResponse response = super.doHandle( webRequest, webResponse, webHandlerChain );
        final PortalRequest request = PortalRequestAccessor.get( webRequest.getRawRequest() );

        final RenderMode mode = request.getMode();

        if ( mode == RenderMode.LIVE || request.getEndpointPath() != null )
        {
            return response;
        }

        final PortalResponse.Builder builder = PortalResponse.create( response );

        if ( mode == RenderMode.INLINE || mode == RenderMode.EDIT )
        {
            builder.header( "X-Frame-Options", "SAMEORIGIN" );
        }

        if ( mode == RenderMode.EDIT )
        {
            builder.removeHeader( "Content-Security-Policy" );
        }
        else if ( !nullToEmpty( previewContentSecurityPolicy ).isBlank() &&
            !response.getHeaders().containsKey( "Content-Security-Policy" ) )
        {
            builder.header( "Content-Security-Policy", previewContentSecurityPolicy );
        }
        return builder.build();
    }

    @Reference
    public void setExceptionMapper( final ExceptionMapper exceptionMapper )
    {
        this.exceptionMapper = exceptionMapper;
    }

    @Reference
    public void setExceptionRenderer( final ExceptionRenderer exceptionRenderer )
    {
        this.exceptionRenderer = exceptionRenderer;
    }
}
