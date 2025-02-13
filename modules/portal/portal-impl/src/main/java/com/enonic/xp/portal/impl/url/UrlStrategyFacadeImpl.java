package com.enonic.xp.portal.impl.url;

import java.util.Objects;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

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
import com.enonic.xp.portal.url.AttachmentUrlGeneratorParams;
import com.enonic.xp.portal.url.AttachmentUrlParams;
import com.enonic.xp.portal.url.BaseUrlStrategy;
import com.enonic.xp.portal.url.ImageUrlGeneratorParams;
import com.enonic.xp.portal.url.ImageUrlParams;
import com.enonic.xp.portal.url.PageUrlGeneratorParams;
import com.enonic.xp.portal.url.PageUrlParams;
import com.enonic.xp.portal.url.UrlStrategyFacade;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.site.Site;

@Component(immediate = true, service = UrlStrategyFacade.class)
public class UrlStrategyFacadeImpl
    implements UrlStrategyFacade
{

    private final ContentService contentService;

    private final ProjectService projectService;

    @Activate
    public UrlStrategyFacadeImpl( @Reference final ContentService contentService, @Reference final ProjectService projectService )
    {
        this.contentService = contentService;
        this.projectService = projectService;
    }

    @Override
    public ImageUrlGeneratorParams offlineImageUrlParams( final ImageUrlParams params )
    {
        final ProjectName mediaPathProjectName = offlineProjectName( params.getProjectName() );
        final Branch mediaPathBranch = offlineBranch( params.getBranch() );
        final ProjectName prefixAndBaseUrlProjectName = offlineBaseUrlProjectName( params.getProjectName() );
        final Branch prefixAndBaseUrlBranch = offlineBaseUrlBranch( params.getBranch() );

        final Media media = resolveMedia( mediaPathProjectName, mediaPathBranch, params.getId(), params.getPath() );

        final String baseUriKey = params.getBaseUrlKey();

        final Site site = baseUriKey != null ? offlineNearestSite( prefixAndBaseUrlProjectName, prefixAndBaseUrlBranch, baseUriKey ) : null;

        final BaseUrlStrategy baseUrlStrategy =
            offlineBaseUrlStrategy( prefixAndBaseUrlProjectName, prefixAndBaseUrlBranch, site != null ? site : media, params.getType() );

        return ImageUrlGeneratorParams.create()
            .setBaseUrlStrategy( baseUrlStrategy )
            .setMedia( media )
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

    @Override
    public ImageUrlGeneratorParams requestImageUrlParams( final ImageUrlParams params )
    {
        final PortalRequest portalRequest = params.getPortalRequest();
        final ProjectName mediaPathProjectName = requestProjectName( params.getProjectName(), portalRequest );
        final Branch mediaPathBranch = requestBranch( params.getBranch(), portalRequest );
        final Media media = resolveMedia( mediaPathProjectName, mediaPathBranch, params.getId(), params.getPath() );

        final BaseUrlStrategy baseUrlStrategy = requestBaseUrlStrategy( portalRequest, params.getType() );

        return ImageUrlGeneratorParams.create()
            .setBaseUrlStrategy( baseUrlStrategy )
            .setMedia( media )
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

    @Override
    public AttachmentUrlGeneratorParams offlineAttachmentUrlParams( final AttachmentUrlParams params )
    {
        final ProjectName mediaPathProjectName = offlineProjectName( params.getProjectName() );
        final Branch mediaPathBranch = offlineBranch( params.getBranch() );
        final ProjectName prefixAndBaseUrlProjectName = offlineBaseUrlProjectName( params.getProjectName() );
        final Branch prefixAndBaseUrlBranch = offlineBaseUrlBranch( params.getBranch() );

        final String baseUriKey = params.getBaseUrlKey();

        final Media media = resolveMedia( mediaPathProjectName, mediaPathBranch, params.getId(), params.getPath() );

        final Site site = baseUriKey != null ? offlineNearestSite( prefixAndBaseUrlProjectName, prefixAndBaseUrlBranch, baseUriKey ) : null;

        final BaseUrlStrategy baseUrlStrategy =
            offlineBaseUrlStrategy( prefixAndBaseUrlProjectName, prefixAndBaseUrlBranch, site != null ? site : media, params.getType() );

        return AttachmentUrlGeneratorParams.create()
            .setBaseUrlStrategy( baseUrlStrategy )
            .setProjectName( mediaPathProjectName )
            .setBranch( mediaPathBranch )
            .setMedia( media )
            .setDownload( params.isDownload() )
            .setName( params.getName() )
            .setLabel( params.getLabel() )
            .addQueryParams( params.getParams().asMap() )
            .build();
    }

    @Override
    public AttachmentUrlGeneratorParams requestAttachmentUrlParams( final AttachmentUrlParams params )
    {
        final PortalRequest portalRequest = params.getPortalRequest();

        final ProjectName mediaPathProjectName = requestProjectName( params.getProjectName(), portalRequest );

        final Branch mediaPathBranch = requestBranch( params.getBranch(), portalRequest );

        final Media media = resolveMedia( mediaPathProjectName, mediaPathBranch, params.getId(), params.getPath() );

        final BaseUrlStrategy baseUrlStrategy = requestBaseUrlStrategy( portalRequest, params.getType() );

        return AttachmentUrlGeneratorParams.create()
            .setBaseUrlStrategy( baseUrlStrategy )
            .setProjectName( mediaPathProjectName )
            .setBranch( mediaPathBranch )
            .setMedia( media )
            .setDownload( params.isDownload() )
            .setName( params.getName() )
            .setLabel( params.getLabel() )
            .addQueryParams( params.getParams().asMap() )
            .build();
    }

    @Override
    public PageUrlGeneratorParams offlinePageUrlParams( final PageUrlParams params )
    {
        final Content content = getContent( Objects.requireNonNullElse( params.getId(), params.getPath() ) );

        final ProjectName projectName = offlineBaseUrlProjectName( params.getProjectName() );
        final Branch branch = offlineBaseUrlBranch( params.getBranch() );

        final BaseUrlStrategy baseUrlStrategy = OfflinePageBaseUrlStrategy.create()
            .contentService( contentService )
            .projectService( projectService )
            .projectName( projectName )
            .branch( branch )
            .content( content )
            .urlType( params.getType() )
            .build();

        return new PageUrlGeneratorParams( baseUrlStrategy );
    }

    @Override
    public PageUrlGeneratorParams requestPageUrlParams( final PageUrlParams params )
    {
        final BaseUrlStrategy baseUrlStrategy = PageRequestBaseUrlStrategy.create()
            .setPortalRequest( params.getPortalRequest() )
            .setUrlType( params.getType() )
            .setId( params.getId() )
            .setPath( params.getPath() )
            .setContentService( contentService )
            .build();

        return new PageUrlGeneratorParams( baseUrlStrategy );
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

    private BaseUrlStrategy offlineBaseUrlStrategy( final ProjectName projectName, final Branch branch, final Content content,
                                                    final String urlType )
    {
        return OfflineBaseUrlStrategy.create()
            .contentService( contentService )
            .projectService( projectService )
            .projectName( projectName )
            .branch( branch )
            .content( content )
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
