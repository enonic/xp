package com.enonic.xp.portal.impl.url;

import java.util.function.Supplier;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.url.BaseUrlParams;
import com.enonic.xp.portal.url.BaseUrlStrategy;
import com.enonic.xp.portal.url.ComponentUrlParams;
import com.enonic.xp.portal.url.PageUrlParams;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.site.Site;

@Component(immediate = true, service = UrlGeneratorParamsAdapter.class)
public class UrlGeneratorParamsAdapter
{

    private final ContentService contentService;

    private final ProjectService projectService;

    @Activate
    public UrlGeneratorParamsAdapter( @Reference final ContentService contentService, @Reference final ProjectService projectService )
    {
        this.contentService = contentService;
        this.projectService = projectService;
    }

    public BaseUrlStrategy noRequestMediaBaseUrlStrategy( final String baseUrl )
    {
        if ( baseUrl != null )
        {
            return new CustomBaseUrlStrategy( baseUrl );
        }
        else
        {
            return () -> "/api";
        }
    }

    public BaseUrlStrategy requestMediaBaseUrlStrategy( final String baseUrl, final String urlType, final String mediaType )
    {
        final PortalRequest portalRequest = PortalRequestAccessor.get();

        if ( baseUrl != null )
        {
            return new CustomBaseUrlStrategy( baseUrl );
        }
        else if ( portalRequest.getBaseUri().startsWith( "/api/" ) )
        {
            return SlashApiRewritableBaseUrlStrategy.forApi( ApplicationKey.from( "media" ), mediaType, portalRequest, urlType );
        }
        else if ( portalRequest.isSiteBase() )
        {
            return SiteRequestBaseUrlStrategy.create()
                .setContentService( contentService )
                .setPortalRequest( portalRequest )
                .setUrlType( urlType )
                .build();
        }
        else
        {
            return NonSiteRequestBaseUrlStrategy.create().setPortalRequest( portalRequest ).setUrlType( urlType ).build();
        }
    }

    public BaseUrlStrategy contentBaseUrlStrategy( final BaseUrlParams params )
    {
        return () -> {
            final String baseUrl = new ContentBaseUrlResolver( contentService, projectService, params ).resolve( metadata -> null );

            final PortalRequest portalRequest = PortalRequestAccessor.get();
            if ( portalRequest != null && portalRequest.isSiteBase() && params.getProjectName() == null && params.getBranch() == null )
            {
                return UrlBuilderHelper.rewriteUri( portalRequest.getRawRequest(), params.getUrlType(), baseUrl );
            }
            return baseUrl;
        };
    }


    public BaseUrlStrategy pageBaseUrlStrategy( final PageUrlParams params )
    {
        final BaseUrlParams baseUrlParams = BaseUrlParams.create()
            .setUrlType( params.getType() )
            .setProjectName( params.getProjectName() )
            .setBranch( params.getBranch() )
            .setId( params.getId() )
            .setPath( params.getPath() )
            .build();

        return () -> {
            final PortalRequest portalRequest = PortalRequestAccessor.get();

            final boolean preferSiteRequest =
                portalRequest != null && portalRequest.isSiteBase() && params.getProjectName() == null && params.getBranch() == null;

            final String baseUrl = new ContentBaseUrlResolver( contentService, projectService, baseUrlParams ).resolve( metadata -> {
                if ( preferSiteRequest )
                {
                    return new ContentPathResolver().portalRequest( portalRequest )
                        .contentService( this.contentService )
                        .id( params.getId() )
                        .path( params.getPath() )
                        .resolve()
                        .toString();
                }
                else if ( metadata.getBaseUrl() == null )
                {
                    return metadata.getContent().getPath().toString();
                }
                else
                {
                    final Site nearestSite = metadata.getNearestSite();
                    final Content content = metadata.getContent();
                    return nearestSite != null
                        ? content.getPath().toString().substring( nearestSite.getPath().toString().length() )
                        : content.getPath().toString();
                }
            } );

            return preferSiteRequest ? UrlBuilderHelper.rewriteUri( portalRequest.getRawRequest(), params.getType(), baseUrl ) : baseUrl;
        };
    }

    public BaseUrlStrategy componentBaseUrlStrategy( final ComponentUrlParams params, final Supplier<String> componentPathSupplier )
    {
        return () -> {
            final PageUrlParams pageUrlParams = new PageUrlParams().type( params.getType() )
                .id( params.getId() )
                .path( params.getPath() )
                .projectName( params.getProjectName() )
                .branch( params.getBranch() );

            final StringBuilder result = new StringBuilder();

            result.append( pageBaseUrlStrategy( pageUrlParams ).generateBaseUrl() );

            final String componentPath = componentPathSupplier.get();
            if ( componentPath != null )
            {
                UrlBuilderHelper.appendPart( result, "_" );
            }

            return result.toString();
        };
    }

//    private String resolveApiApplication( final String application, final ApplicationKey applicationFromRequest )
//    {
//        String result = application;
//        if ( application == null && applicationFromRequest != null )
//        {
//            result = applicationFromRequest.toString();
//        }
//        if ( result == null )
//        {
//            throw new IllegalArgumentException( "Application must be provided" );
//        }
//        return result;
//    }

//    public ApiUrlGeneratorParams requestApiUrlParams( final ApiUrlParams params )
//    {
//        final PortalRequest portalRequest = PortalRequestAccessor.get();
//
//        final String application = resolveApiApplication( params.getApplication(), portalRequest.getApplicationKey() );
//
//        final BaseUrlStrategy baseUrlStrategy = ApiRequestBaseUrlStrategy.create()
//            .setContentService( contentService )
//            .setPortalRequest( portalRequest )
//            .setUrlType( params.getType() )
//            .build();
//
//        return ApiUrlGeneratorParams.create()
//            .setBaseUrlStrategy( baseUrlStrategy )
//            .setApplication( application )
//            .setApi( params.getApi() )
//            .setPath( () -> resolveApiPath( params ) )
//            .addQueryParams( params.getQueryParams() )
//            .build();
//    }
//
//    public ApiUrlGeneratorParams offlineApiUrlParams( final ApiUrlParams params )
//    {
//        final BaseUrlStrategy baseUrlStrategy;
//        if ( params.getBaseUrl() != null )
//        {
//            baseUrlStrategy = new CustomBaseUrlStrategy( params.getBaseUrl() );
//        }
//        else
//        {
//            baseUrlStrategy = () -> "/api";
//        }
//
//        return ApiUrlGeneratorParams.create()
//            .setBaseUrlStrategy( baseUrlStrategy )
//            .setApplication( Objects.requireNonNull( params.getApplication(), "Application must be provided" ) )
//            .setApi( params.getApi() )
//            .setPath( () -> resolveApiPath( params ) )
//            .addQueryParams( params.getQueryParams() )
//            .build();
//    }

//    private String resolveApiPath( final ApiUrlParams params )
//    {
//        final StringBuilder path = new StringBuilder();
//
//        appendSubPath( path, params.getPath() );
//        appendPathSegments( path, params.getPathSegments() );
//
//        return path.toString();
//    }
}
