package com.enonic.xp.portal.impl.url;

import java.util.Objects;
import java.util.function.Supplier;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

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
import com.enonic.xp.portal.url.BaseUrlStrategy;
import com.enonic.xp.portal.url.ImageUrlGeneratorParams;
import com.enonic.xp.portal.url.ImageUrlParams;
import com.enonic.xp.portal.url.PageUrlGeneratorParams;
import com.enonic.xp.portal.url.PageUrlParams;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.site.Site;

import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendPart;
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
        final ProjectName baseUrlProjectName = offlineBaseUrlProjectName( params.getProjectName() );
        final Branch baseUrlBranch = offlineBaseUrlBranch( params.getBranch() );

        final Supplier<Media> mediaSupplier = () -> resolveMedia( mediaPathProjectName, mediaPathBranch, params.getId(), params.getPath() );

        final Supplier<Content> baseUrlContentSupplier = () -> {
            final String baseUriKey = params.getBaseUrlKey();

            final Site site = baseUriKey != null ? offlineNearestSite( baseUrlProjectName, baseUrlBranch, params.getBaseUrlKey() ) : null;

            return site != null ? site : mediaSupplier.get();
        };

        final BaseUrlStrategy baseUrlStrategy =
            offlineBaseUrlStrategy( baseUrlProjectName, baseUrlBranch, baseUrlContentSupplier, params.getType() );

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
        final ProjectName prefixAndBaseUrlProjectName = offlineBaseUrlProjectName( params.getProjectName() );
        final Branch prefixAndBaseUrlBranch = offlineBaseUrlBranch( params.getBranch() );

        final Supplier<Media> mediaSupplier = () -> resolveMedia( mediaPathProjectName, mediaPathBranch, params.getId(), params.getPath() );

        final Supplier<Content> baseUrlContentSupplier = () -> {
            final String baseUriKey = params.getBaseUrlKey();
            final Site site =
                baseUriKey != null ? offlineNearestSite( prefixAndBaseUrlProjectName, prefixAndBaseUrlBranch, baseUriKey ) : null;
            return site != null ? site : mediaSupplier.get();
        };

        final BaseUrlStrategy baseUrlStrategy =
            offlineBaseUrlStrategy( prefixAndBaseUrlProjectName, prefixAndBaseUrlBranch, baseUrlContentSupplier, params.getType() );

        return AttachmentUrlGeneratorParams.create()
            .setBaseUrlStrategy( baseUrlStrategy )
            .setProjectName( mediaPathProjectName )
            .setBranch( mediaPathBranch )
            .setMedia( mediaSupplier )
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
            .setMedia( () -> resolveMedia( mediaPathProjectName, mediaPathBranch, params.getId(),
                                           Objects.requireNonNullElseGet( params.getPath(),
                                                                          () -> portalRequest.getContentPath().toString() ) ) )
            .setDownload( params.isDownload() )
            .setName( params.getName() )
            .setLabel( params.getLabel() )
            .addQueryParams( params.getParams().asMap() )
            .build();
    }

    public PageUrlGeneratorParams offlinePageUrlParams( final PageUrlParams params )
    {
        final ProjectName projectName = offlineBaseUrlProjectName( params.getProjectName() );
        final Branch branch = offlineBaseUrlBranch( params.getBranch() );

        final BaseUrlStrategy baseUrlStrategy = PageOfflineBaseUrlStrategy.create()
            .contentService( contentService )
            .projectService( projectService )
            .projectName( projectName )
            .branch( branch )
            .content( () -> getContent( Objects.requireNonNullElse( params.getId(), params.getPath() ) ) )
            .urlType( params.getType() )
            .build();

        final PageUrlGeneratorParams.Builder builder = PageUrlGeneratorParams.create().setBaseUrlStrategy( baseUrlStrategy );
        if ( params.getParams() != null )
        {
            builder.addQueryParams( params.getParams().asMap() );
        }
        return builder.build();
    }

    public PageUrlGeneratorParams requestPageUrlParams( final PageUrlParams params )
    {
        final BaseUrlStrategy baseUrlStrategy = PageRequestBaseUrlStrategy.create()
            .setUrlType( params.getType() )
            .setId( params.getId() )
            .setPath( params.getPath() )
            .setContentService( contentService )
            .build();

        final PageUrlGeneratorParams.Builder builder = PageUrlGeneratorParams.create().setBaseUrlStrategy( baseUrlStrategy );
        if ( params.getParams() != null )
        {
            builder.addQueryParams( params.getParams().asMap() );
        }
        return builder.build();
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
            .setPath( () -> {
                final StringBuilder path = new StringBuilder();

                appendSubPath( path, params.getPath() );
                appendPathSegments( path, params.getPathSegments() );

                return path.toString();
            } )
            .addQueryParams( params.getQueryParams() )
            .build();
    }

    public ApiUrlGeneratorParams offlineApiUrlParams( final ApiUrlParams params )
    {
        return ApiUrlGeneratorParams.create()
            .setBaseUrlStrategy( () -> "/api" )
            .setApplication( params.getApplication() )
            .setApi( params.getApi() )
            .setPath( params::getPath )
            .addQueryParams( params.getQueryParams() )
            .build();
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

    private ProjectName offlineBaseUrlProjectName( final String projectName )
    {
        return ContextAccessor.current().getRepositoryId() != null
            ? ProjectName.from( ContextAccessor.current().getRepositoryId() )
            : ProjectName.from( Objects.requireNonNull( projectName, "Project must be provided" ) );
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

    private Branch offlineBaseUrlBranch( final String branch )
    {
        return ContextAccessor.current().getBranch() != null
            ? ContextAccessor.current().getBranch()
            : Objects.requireNonNullElse( Branch.from( branch ), ContentConstants.BRANCH_MASTER );
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
