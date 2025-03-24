package com.enonic.xp.portal.impl.url;

import java.net.URI;
import java.util.Objects;
import java.util.function.Supplier;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.base.Suppliers;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Media;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.exception.NotFoundException;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.url.ApiUrlGeneratorParams;
import com.enonic.xp.portal.url.ApiUrlParams;
import com.enonic.xp.portal.url.AttachmentUrlGeneratorParams;
import com.enonic.xp.portal.url.AttachmentUrlParams;
import com.enonic.xp.portal.url.BaseUrlParams;
import com.enonic.xp.portal.url.BaseUrlStrategy;
import com.enonic.xp.portal.url.ImageUrlGeneratorParams;
import com.enonic.xp.portal.url.ImageUrlParams;
import com.enonic.xp.portal.url.PageUrlParams;
import com.enonic.xp.portal.url.UrlTypeConstants;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.site.Site;

import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendPathSegments;
import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendSubPath;

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

    public ImageUrlGeneratorParams offlineImageUrlParams( final ImageUrlParams params )
    {
        final ProjectName mediaPathProjectName = offlineProjectName( params.getProjectName() );
        final Branch mediaPathBranch = offlineBranch( params.getBranch() );

        final Supplier<Media> mediaSupplier =
            Suppliers.memoize( () -> resolveMedia( mediaPathProjectName, mediaPathBranch, params.getId(), params.getPath() ) );

        final BaseUrlStrategy baseUrlStrategy =
            resolveBaseUrlStrategy( params.getBaseUrl(), params.getBaseUrlParams(), mediaSupplier, params.getType() );

        return ImageUrlGeneratorParams.create()
            .setBaseUrlStrategy( baseUrlStrategy )
            .setMedia( mediaSupplier )
            .setProjectName( mediaPathProjectName )
            .setBranch( mediaPathBranch )
            .setScale( params.getScale() )
            .setFormat( params.getFormat() )
            .setFilter( params.getFilter() )
            .setQuality( params.getQuality() )
            .setBackground( params.getBackground() )
            .addQueryParams( params.getParams().asMap() )
            .build();
    }

    public ImageUrlGeneratorParams requestImageUrlParams( final ImageUrlParams params )
    {
        final PortalRequest portalRequest = PortalRequestAccessor.get();
        final ProjectName mediaPathProjectName = requestProjectName( params.getProjectName(), portalRequest );
        final Branch mediaPathBranch = requestBranch( params.getBranch(), portalRequest );

        final BaseUrlStrategy baseUrlStrategy = requestBaseUrlStrategy( portalRequest, params.getType() );

        return ImageUrlGeneratorParams.create()
            .setBaseUrlStrategy( baseUrlStrategy )
            .setMedia( () -> resolveMedia( mediaPathProjectName, mediaPathBranch, params.getId(),
                                           Objects.requireNonNullElseGet( params.getPath(),
                                                                          () -> portalRequest.getContentPath().toString() ) ) )
            .setProjectName( mediaPathProjectName )
            .setBranch( mediaPathBranch )
            .setScale( params.getScale() )
            .setFormat( params.getFormat() )
            .setFilter( params.getFilter() )
            .setQuality( params.getQuality() )
            .setBackground( params.getBackground() )
            .addQueryParams( params.getParams().asMap() )
            .build();
    }

    public AttachmentUrlGeneratorParams offlineAttachmentUrlParams( final AttachmentUrlParams params )
    {
        final ProjectName mediaPathProjectName = offlineProjectName( params.getProjectName() );
        final Branch mediaPathBranch = offlineBranch( params.getBranch() );

        final Supplier<Content> contentSupplier = Suppliers.memoize( () -> ContextBuilder.copyOf( ContextAccessor.current() )
            .repositoryId( mediaPathProjectName.getRepoId() )
            .branch( mediaPathBranch )
            .build()
            .callWith( () -> getContent( Objects.requireNonNullElse( params.getId(), params.getPath() ) ) ) );

        final BaseUrlStrategy baseUrlStrategy =
            resolveBaseUrlStrategy( params.getBaseUrl(), params.getBaseUrlParams(), contentSupplier, params.getType() );

        return AttachmentUrlGeneratorParams.create()
            .setBaseUrlStrategy( baseUrlStrategy )
            .setProjectName( mediaPathProjectName )
            .setBranch( mediaPathBranch )
            .setContent( contentSupplier )
            .setDownload( params.isDownload() )
            .setName( params.getName() )
            .setLabel( params.getLabel() )
            .addQueryParams( params.getParams().asMap() )
            .build();
    }

    public AttachmentUrlGeneratorParams requestAttachmentUrlParams( final AttachmentUrlParams params )
    {
        final PortalRequest portalRequest = PortalRequestAccessor.get();

        final ProjectName mediaPathProjectName = requestProjectName( params.getProjectName(), portalRequest );

        final Branch mediaPathBranch = requestBranch( params.getBranch(), portalRequest );

        final BaseUrlStrategy baseUrlStrategy = requestBaseUrlStrategy( portalRequest, params.getType() );

        return AttachmentUrlGeneratorParams.create()
            .setBaseUrlStrategy( baseUrlStrategy )
            .setProjectName( mediaPathProjectName )
            .setBranch( mediaPathBranch )
            .setContent( () -> ContextBuilder.copyOf( ContextAccessor.current() )
                .repositoryId( mediaPathProjectName.getRepoId() )
                .branch( mediaPathBranch )
                .build()
                .callWith( () -> {
                    final ContentResolver contentResolver = new ContentResolver().portalRequest( portalRequest )
                        .contentService( this.contentService )
                        .id( params.getId() )
                        .path( params.getPath() );
                    return contentResolver.resolve();
                } ) )
            .setDownload( params.isDownload() )
            .setName( params.getName() )
            .setLabel( params.getLabel() )
            .addQueryParams( params.getParams().asMap() )
            .build();
    }

    public BaseUrlStrategy pageNoRequestBaseUrlStrategy( final PageUrlParams params )
    {
        return PageNoRequestBaseUrlStrategy.create()
            .contentService( contentService )
            .projectService( projectService )
            .projectName( () -> new ContentProjectResolver( params.getProjectName() ).resolve() )
            .branch( () -> new ContentBranchResolver( params.getBranch() ).resolve() )
            .content( () -> getContent( Objects.requireNonNullElse( params.getId(), params.getPath() ) ) )
            .urlType( params.getType() )
            .build();
    }

    public BaseUrlStrategy pageRequestBaseUrlStrategy( final PageUrlParams params )
    {
        final PortalRequest portalRequest = PortalRequestAccessor.get();

        if ( portalRequest.isSiteBase() && params.getProjectName() == null && params.getBranch() == null )
        {
            final ProjectName projectName = ProjectName.from( portalRequest.getRepositoryId() );
            final Branch branch = portalRequest.getBranch();

            return PageSiteRequestBaseUrlStrategy.create()
                .setPortalRequest( portalRequest )
                .setUrlType( params.getType() )
                .setId( params.getId() )
                .setPath( params.getPath() )
                .setProjectName( projectName )
                .setBranch( branch )
                .setContentService( contentService )
                .build();
        }
        else
        {
            return pageNoRequestBaseUrlStrategy( params );
        }
    }

    private String resolveApiApplication( final String application, final ApplicationKey applicationFromRequest )
    {
        String result = application;
        if ( application == null && applicationFromRequest != null )
        {
            result = applicationFromRequest.toString();
        }
        if ( result == null )
        {
            throw new IllegalArgumentException( "Application must be provided" );
        }
        return result;
    }

    public ApiUrlGeneratorParams requestApiUrlParams( final ApiUrlParams params )
    {
        final PortalRequest portalRequest = PortalRequestAccessor.get();

        final String application = resolveApiApplication( params.getApplication(), portalRequest.getApplicationKey() );

        final BaseUrlStrategy baseUrlStrategy = ApiRequestBaseUrlStrategy.create()
            .setContentService( contentService )
            .setPortalRequest( portalRequest )
            .setUrlType( params.getType() )
            .build();

        return ApiUrlGeneratorParams.create()
            .setBaseUrlStrategy( baseUrlStrategy )
            .setApplication( application )
            .setApi( params.getApi() )
            .setPath( () -> resolveApiPath( params ) )
            .addQueryParams( params.getQueryParams() )
            .build();
    }

    public ApiUrlGeneratorParams offlineApiUrlParams( final ApiUrlParams params )
    {
        final BaseUrlStrategy baseUrlStrategy;
        if ( params.getBaseUrl() != null )
        {
            final String path = UrlTypeConstants.SERVER_RELATIVE.equals( params.getType() )
                ? URI.create( params.getBaseUrl() ).getPath()
                : params.getBaseUrl();
            baseUrlStrategy = () -> path.endsWith( "/" ) ? path.substring( 0, path.length() - 1 ) + "/_/" : path + "/_/";
        }
        else if ( params.getBaseUrlParams() != null )
        {
            final BaseUrlParams baseUrlParams = params.getBaseUrlParams();

            final ProjectName projectName = offlineProjectName( baseUrlParams.getProjectName() );
            final Branch branch = offlineBranch( baseUrlParams.getBranch() );
            final String contentKey = baseUrlParams.getKey();

            final Supplier<Content> baseUrlContentSupplier = () -> offlineNearestSite( projectName, branch, contentKey );

            baseUrlStrategy = offlineBaseUrlStrategy( projectName, branch, baseUrlContentSupplier, params.getType() );
        }
        else
        {
            baseUrlStrategy = () -> "/api";
        }

        return ApiUrlGeneratorParams.create()
            .setBaseUrlStrategy( baseUrlStrategy )
            .setApplication( Objects.requireNonNull( params.getApplication(), "Application must be provided" ) )
            .setApi( params.getApi() )
            .setPath( () -> resolveApiPath( params ) )
            .addQueryParams( params.getQueryParams() )
            .build();
    }

    private String resolveApiPath( final ApiUrlParams params )
    {
        final StringBuilder path = new StringBuilder();

        appendSubPath( path, params.getPath() );
        appendPathSegments( path, params.getPathSegments() );

        return path.toString();
    }

    private Media resolveMedia( final ProjectName projectName, final Branch branch, final String id, final String path )
    {
        return ContextBuilder.copyOf( ContextAccessor.current() )
            .repositoryId( projectName.getRepoId() )
            .branch( branch )
            .build()
            .callWith( () -> getMedia( Objects.requireNonNullElse( id, path ) ) );
    }

    private Site offlineNearestSite( final ProjectName projectName, final Branch branch, final String contentKey )
    {
        return ContextBuilder.copyOf( ContextAccessor.current() )
            .repositoryId( projectName.getRepoId() )
            .branch( branch )
            .build()
            .callWith( () -> contentKey.startsWith( "/" )
                ? contentService.findNearestSiteByPath( ContentPath.from( contentKey ) )
                : contentService.getNearestSite( ContentId.from( contentKey ) ) );
    }

    private BaseUrlStrategy offlineBaseUrlStrategy( final ProjectName projectName, final Branch branch,
                                                    final Supplier<Content> contentSupplier, final String urlType )
    {
        return OfflineBaseUrlStrategy.create()
            .contentService( contentService )
            .projectService( projectService )
            .projectName( projectName )
            .branch( branch )
            .content( contentSupplier )
            .urlType( urlType )
            .build();
    }

    private BaseUrlStrategy requestBaseUrlStrategy( final PortalRequest portalRequest, final String urlType )
    {
        return RequestBaseUrlStrategy.create()
            .setContentService( contentService )
            .setPortalRequest( portalRequest )
            .setUrlType( urlType )
            .build();
    }

    private ProjectName offlineProjectName( final String projectName )
    {
        return projectName != null
            ? ProjectName.from( projectName )
            : ProjectName.from( Objects.requireNonNull( ContextAccessor.current().getRepositoryId(), "Project must be provided" ) );
    }

    private ProjectName requestProjectName( final String projectName, final PortalRequest portalRequest )
    {
        return projectName != null
            ? ProjectName.from( projectName )
            : ProjectName.from( Objects.requireNonNull( portalRequest.getRepositoryId(), "Project must be provided" ) );
    }

    private Branch offlineBranch( final String branch )
    {
        return branch != null
            ? Branch.from( branch )
            : Objects.requireNonNullElse( ContextAccessor.current().getBranch(), ContentConstants.BRANCH_MASTER );
    }

    private Branch requestBranch( final String branch, final PortalRequest portalRequest )
    {
        return branch != null
            ? Branch.from( branch )
            : Objects.requireNonNullElse( portalRequest.getBranch(), ContentConstants.BRANCH_MASTER );
    }

    private BaseUrlStrategy resolveBaseUrlStrategy( final String baseUrl, final BaseUrlParams baseUrlParams,
                                                    final Supplier<? extends Content> contentSupplier, final String type )
    {
        if ( baseUrl != null )
        {
            return new CustomBaseUlrStrategy( baseUrl, type );
        }
        else if ( baseUrlParams != null )
        {
            final ProjectName projectName = offlineProjectName( baseUrlParams.getProjectName() );
            final Branch branch = offlineBranch( baseUrlParams.getBranch() );
            final Supplier<Content> baseUrlContentSupplier = () -> {
                final String contentKey = baseUrlParams.getKey();
                final Site site = contentKey != null ? offlineNearestSite( projectName, branch, contentKey ) : null;
                return site != null ? site : contentSupplier.get();
            };
            return offlineBaseUrlStrategy( projectName, branch, baseUrlContentSupplier, type );
        }
        else
        {
            throw new IllegalArgumentException( "Either baseUrl or baseUrlParams must be provided" );
        }
    }

    private Content getContent( final String contentKey )
    {
        if ( contentKey.startsWith( "/" ) )
        {
            return contentService.getByPath( ContentPath.from( contentKey ) );
        }
        else
        {
            return contentService.getById( ContentId.from( contentKey ) );
        }
    }

    private Media getMedia( final String contentKey )
    {
        Content content = getContent( contentKey );

        if ( !( content instanceof Media ) )
        {
            throw new NotFoundException( String.format( "Content [%s] is not a Media", contentKey ) )
            {
            };
        }

        return (Media) content;
    }

}
