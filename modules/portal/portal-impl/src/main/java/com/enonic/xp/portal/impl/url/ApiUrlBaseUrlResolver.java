package com.enonic.xp.portal.impl.url;

import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.impl.PortalRequestHelper;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.webapp.WebappDescriptor;
import com.enonic.xp.webapp.WebappService;

import static java.util.Objects.requireNonNull;

final class ApiUrlBaseUrlResolver
    implements Supplier<String>
{
    private final DescriptorKey descriptorKey;

    private final String baseUrl;

    private final String apiBaseUrl;

    private final String urlType;

    private final String defaultMediaBaseUrl;

    private final boolean mediaApiAutoMount;

    private final WebappService webappService;

    private final SiteService siteService;

    ApiUrlBaseUrlResolver( final Builder builder )
    {
        this.descriptorKey = requireNonNull( builder.descriptorKey, "DescriptorKey must be set" );
        this.baseUrl = builder.baseUrl;
        this.apiBaseUrl = builder.apiBaseUrl;
        this.urlType = builder.urlType;
        this.defaultMediaBaseUrl = builder.defaultMediaBaseUrl;
        this.mediaApiAutoMount = builder.mediaApiAutoMount;
        this.webappService = requireNonNull( builder.webappService, "WebappService must be set" );
        this.siteService = requireNonNull( builder.siteService, "SiteService must be set" );
    }

    static Builder create()
    {
        return new Builder();
    }

    static final class Builder
    {
        private DescriptorKey descriptorKey;

        private String baseUrl;

        private String apiBaseUrl;

        private String urlType;

        private String defaultMediaBaseUrl;

        private boolean mediaApiAutoMount = true;

        private WebappService webappService;

        private SiteService siteService;

        Builder setDescriptorKey( final DescriptorKey descriptorKey )
        {
            this.descriptorKey = descriptorKey;
            return this;
        }

        Builder setBaseUrl( final String baseUrl )
        {
            this.baseUrl = baseUrl;
            return this;
        }

        Builder setApiBaseUrl( final String apiBaseUrl )
        {
            this.apiBaseUrl = apiBaseUrl;
            return this;
        }

        Builder setUrlType( final String urlType )
        {
            this.urlType = urlType;
            return this;
        }

        Builder setDefaultMediaBaseUrl( final String defaultMediaBaseUrl )
        {
            this.defaultMediaBaseUrl = defaultMediaBaseUrl;
            return this;
        }

        Builder setMediaApiAutoMount( final boolean mediaApiAutoMount )
        {
            this.mediaApiAutoMount = mediaApiAutoMount;
            return this;
        }

        Builder setWebappService( final WebappService webappService )
        {
            this.webappService = webappService;
            return this;
        }

        Builder setSiteService( final SiteService siteService )
        {
            this.siteService = siteService;
            return this;
        }

        ApiUrlBaseUrlResolver build()
        {
            return new ApiUrlBaseUrlResolver( this );
        }
    }

    @Override
    public String get()
    {
        final PortalRequest portalRequest = PortalRequestAccessor.get();

        // the root where this API is exposed, verbatim: nothing but path and query follow
        if ( apiBaseUrl != null )
        {
            return apiBaseUrl;
        }

        if ( baseUrl != null )
        {
            final StringBuilder url = new StringBuilder( baseUrl );
            UrlBuilderHelper.appendPart( url, "_" );
            UrlBuilderHelper.appendPart( url, descriptorKey.toString() );
            return url.toString();
        }

        final boolean mediaApi = ApplicationKey.MEDIA_MOD.equals( descriptorKey.getApplicationKey() );

        if ( portalRequest == null || portalRequest.getBaseUri() == null || portalRequest.getBaseUri().isEmpty() )
        {
            final String declaredLocation = declaredApiLocation();
            if ( declaredLocation != null )
            {
                return declaredLocation;
            }

            return mediaApi && defaultMediaBaseUrl != null ? defaultMediaBaseUrlForm() : apiForm();
        }

        final String baseUri = portalRequest.getBaseUri();

        if ( baseUri.startsWith( "/api/" ) )
        {
            // an API location declared for the current context wins over the default media
            // base and over the sibling assumption
            final String declaredLocation = declaredApiLocation();
            if ( declaredLocation != null )
            {
                return declaredLocation;
            }

            if ( mediaApi && defaultMediaBaseUrl != null )
            {
                return defaultMediaBaseUrlForm();
            }

            // APIs are expected to be mounted next to the current endpoint: resolve the public
            // base of the endpoint itself (always within vhost scope) and address its sibling
            final String endpointBase =
                UrlBuilderHelper.rewriteUri( portalRequest.getRawRequest(), urlType, portalRequest.getBaseUri() );
            final StringBuilder url = new StringBuilder( endpointBase.substring( 0, endpointBase.lastIndexOf( '/' ) ) );
            UrlBuilderHelper.appendPart( url, descriptorKey.toString() );
            return url.toString();
        }

        // a site serves media under its "_" endpoint only while media APIs are auto-mounted
        // (the legacy default) or the site mounts them explicitly; otherwise generation diverts
        // where dispatch would 404: the default media base, or the canonical /api form.
        // For a site rendered inside an admin tool (edit/preview) the admin tool rule applies
        // instead: media URLs anchor at the hosting tool's own "_" endpoint, staying within the
        // authenticated admin session
        if ( mediaApi && !mediaApiAutoMount && PortalRequestHelper.isSiteBase( portalRequest ) && !isMountedOnSite( portalRequest ) )
        {
            if ( baseUri.startsWith( "/admin/" ) )
            {
                final StringBuilder url = new StringBuilder( adminToolBase( baseUri ) );
                UrlBuilderHelper.appendPart( url, "_" );
                UrlBuilderHelper.appendPart( url, descriptorKey.toString() );
                return UrlBuilderHelper.rewriteUri( portalRequest.getRawRequest(), urlType, url.toString() );
            }

            final String declaredLocation = declaredApiLocation();
            if ( declaredLocation != null )
            {
                return declaredLocation;
            }

            return defaultMediaBaseUrl != null ? defaultMediaBaseUrlForm() : apiForm();
        }

        // admin mounts always keep "_"-anchored media URLs: that is how they stay within the
        // authenticated admin session. Only a webapp that does not mount the media APIs diverts
        // to a location declared for the current context or to the default media base;
        // without either, the webapp "_" form is kept.
        if ( mediaApi && !PortalRequestHelper.isSiteBase( portalRequest ) && baseUri.startsWith( "/webapp/" ) &&
            !isMountedOnWebapp( baseUri ) )
        {
            final String declaredLocation = declaredApiLocation();
            if ( declaredLocation != null )
            {
                return declaredLocation;
            }

            if ( defaultMediaBaseUrl != null )
            {
                return defaultMediaBaseUrlForm();
            }
        }

        final StringBuilder url = new StringBuilder( generateBaseUrlPrefix( portalRequest ) );
        UrlBuilderHelper.appendPart( url, descriptorKey.toString() );
        return UrlBuilderHelper.rewriteUri( portalRequest.getRawRequest(), urlType, url.toString() );
    }

    private String declaredApiLocation()
    {
        // declared for the current context - by the matched vhost mapping, or by whatever
        // else established the attributes; used verbatim
        return ApiLocationResolver.resolve( descriptorKey );
    }

    private String defaultMediaBaseUrlForm()
    {
        // the default media base points directly at the API root: no "_" endpoint segment is added
        final StringBuilder url = new StringBuilder( defaultMediaBaseUrl );
        UrlBuilderHelper.appendPart( url, descriptorKey.toString() );
        return url.toString();
    }

    private String apiForm()
    {
        final StringBuilder url = new StringBuilder( "/api" );
        UrlBuilderHelper.appendPart( url, descriptorKey.toString() );
        return url.toString();
    }

    private static String adminToolBase( final String baseUri )
    {
        // baseUri of a site rendered inside an admin tool is /admin/<app>/<tool>/<mode>:
        // the anchor is the tool base itself
        final int appEnd = baseUri.indexOf( '/', "/admin/".length() );
        if ( appEnd == -1 )
        {
            return baseUri;
        }
        final int toolEnd = baseUri.indexOf( '/', appEnd + 1 );
        return toolEnd == -1 ? baseUri : baseUri.substring( 0, toolEnd );
    }

    private boolean isMountedOnSite( final PortalRequest portalRequest )
    {
        return ApiMountVerifier.isApiMountedOnSite( descriptorKey, PortalRequestHelper.getSiteConfigs( portalRequest ), siteService,
                                                    false );
    }

    private boolean isMountedOnWebapp( final String baseUri )
    {
        final WebappDescriptor descriptor =
            webappService.getDescriptor( ApplicationKey.from( baseUri.substring( "/webapp/".length() ) ) );
        return descriptor != null && descriptor.getApiMounts().contains( descriptorKey );
    }

    private String generateBaseUrlPrefix( final PortalRequest portalRequest )
    {
        final StringBuilder url = new StringBuilder();

        if ( PortalRequestHelper.isSiteBase( portalRequest ) )
        {
            url.append( portalRequest.getBaseUri() );
            UrlBuilderHelper.appendSubPath( url, ProjectName.from( portalRequest.getRepositoryId() ).toString() );
            UrlBuilderHelper.appendSubPath( url, portalRequest.getBranch().getValue() );

            if ( portalRequest.getSite() != null )
            {
                UrlBuilderHelper.appendAndEncodePathParts( url, portalRequest.getSite().getPath().toString() );
            }

            UrlBuilderHelper.appendPart( url, "_" );
        }
        else if ( "/admin".equals( portalRequest.getBaseUri() ) )
        {
            url.append( "/admin/com.enonic.xp.app.main/home/_/" );
        }
        else
        {
            UrlBuilderHelper.appendSubPath( url, portalRequest.getBaseUri() );
            UrlBuilderHelper.appendPart( url, "_" );
        }

        return url.toString();
    }
}
