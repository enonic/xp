package com.enonic.xp.portal.impl.url3;

import java.util.Objects;

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
import com.enonic.xp.portal.impl.ContentResolver;
import com.enonic.xp.portal.url.BaseUrlStrategy;
import com.enonic.xp.portal.url.ImageUrlGeneratorParams;
import com.enonic.xp.portal.url.ImageUrlParams;
import com.enonic.xp.portal.url.PathPrefixStrategy;
import com.enonic.xp.portal.url.RewritePathStrategy;
import com.enonic.xp.portal.url.UrlStrategyFacade;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;

import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendPart;

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

    private String resolveBaseUrl( final SiteConfigs siteConfigs )
    {
        SiteConfig siteConfig = siteConfigs.get( ApplicationKey.from( "com.enonic.xp.site" ) );
        if ( siteConfig != null )
        {
            return siteConfig.getConfig().getString( "baseUrl" );
        }
        return null;
    }

    @Override
    public BaseUrlStrategy offlineBaseUrlStrategy(final ProjectName projectName, final Branch branch, final Content content) {
        if (content == null) {
            return () -> "/";
        }

        if (content instanceof Site site) {
            String baseUrl = resolveBaseUrl(site.getSiteConfigs());
            if (baseUrl != null) {
                return () -> baseUrl;
            }
        }

        Site nearestSite = ContextBuilder.copyOf( ContextAccessor.current() )
            .repositoryId( projectName.getRepoId() )
            .branch( branch )
            .build()
            .callWith( () -> contentService.getNearestSite( ContentId.from( content.getId() ) ) );

        if (nearestSite != null) {
            String baseUrl = resolveBaseUrl(nearestSite.getSiteConfigs());
            if (baseUrl != null) {
                return () -> baseUrl;
            }
        }

        Project project = projectService.get( projectName);
        if (project != null) {
            String baseUrl = resolveBaseUrl(project.getSiteConfigs());
            if (baseUrl != null) {
                return () -> baseUrl;
            }
        }

        return () -> "/";
    }

    @Override
    public BaseUrlStrategy requestBaseUrlStrategy( final PortalRequest portalRequest, final String urlType )
    {
        return new RequestBaseUrlStrategy( portalRequest, urlType );
    }

    @Override
    public RewritePathStrategy requestRewriteStrategy( final PortalRequest portalRequest )
    {
        return new RequestRewritePathStrategy( portalRequest );
    }

    @Override
    public RewritePathStrategy doNotRewriteStrategy()
    {
        return path -> path;
    }

    @Override
    public ImageUrlGeneratorParams offlineImageUrlParams( final ImageUrlParams params )
    {
        final ProjectName mediaPathProjectName = params.getProjectName() != null
            ? ProjectName.from( params.getProjectName() )
            : ProjectName.from( Objects.requireNonNull( ContextAccessor.current().getRepositoryId() ) );

        final Branch mediaPathBranch = params.getBranch() != null
            ? Branch.from( params.getBranch() )
            : Objects.requireNonNullElse( ContextAccessor.current().getBranch(), ContentConstants.BRANCH_MASTER );

        final ProjectName prefixAndBaseUrlProjectName = ContextAccessor.current().getRepositoryId() != null
            ? ProjectName.from( ContextAccessor.current().getRepositoryId() )
            : ProjectName.from( Objects.requireNonNull( params.getProjectName() ) );

        final Branch prefixAndBaseUrlBranch = ContextAccessor.current().getBranch() != null
            ? ContextAccessor.current().getBranch()
            : Objects.requireNonNullElse( Branch.from( params.getBranch() ), ContentConstants.BRANCH_MASTER );

        final String contentKey = params.getContentKey();

        final Media media = ContextBuilder.copyOf( ContextAccessor.current() )
            .repositoryId( mediaPathProjectName.getRepoId() )
            .branch( mediaPathBranch )
            .build()
            .callWith( () -> getMedia( Objects.requireNonNullElse( params.getId(), params.getPath() ) ) );

        final Site site = ContextBuilder.copyOf( ContextAccessor.current() )
            .repositoryId( prefixAndBaseUrlProjectName.getRepoId() )
            .branch( prefixAndBaseUrlBranch )
            .build()
            .callWith( () -> contentKey.startsWith( "/" )
                ? contentService.findNearestSiteByPath( ContentPath.from( contentKey ) )
                : contentService.getNearestSite( ContentId.from( contentKey ) ) );

        final BaseUrlStrategy baseUrlStrategy = offlineBaseUrlStrategy( prefixAndBaseUrlProjectName, prefixAndBaseUrlBranch, site );

        final PathPrefixStrategy pathPrefixStrategy = contentKey == null ? () -> "/api" : () -> {
            final StringBuilder prefix = new StringBuilder();

            appendPart( prefix, "site" );
            appendPart( prefix, prefixAndBaseUrlProjectName.toString() );
            appendPart( prefix, prefixAndBaseUrlBranch.getValue() );
            if ( site != null )
            {
                appendPart( prefix, site.getPath().toString() );
            }
            appendPart( prefix, "_" );
            return prefix.toString();
        };

        final RewritePathStrategy rewritePathStrategy = doNotRewriteStrategy();

        final ImageUrlGeneratorParams generatorParams = new ImageUrlGeneratorParams();

        generatorParams.baseUrlStrategy = baseUrlStrategy;
        generatorParams.pathPrefixStrategy = pathPrefixStrategy;
        generatorParams.rewritePathStrategy = rewritePathStrategy;

        generatorParams.mediaProvider = () -> media;

        generatorParams.projectName = mediaPathProjectName;
        generatorParams.branch = mediaPathBranch;
        generatorParams.scale = params.getScale();
        generatorParams.format = params.getFormat();
        generatorParams.filter = params.getFilter();
        generatorParams.quality = params.getQuality();
        generatorParams.background = params.getBackground();

        return generatorParams;
    }

    @Override
    public ImageUrlGeneratorParams requestImageUrlParams( final ImageUrlParams params )
    {
        final PortalRequest portalRequest = PortalRequestAccessor.get();

        final ProjectName mediaPathProjectName = params.getProjectName() != null
            ? ProjectName.from( params.getProjectName() )
            : ProjectName.from( Objects.requireNonNull( portalRequest.getRepositoryId(), "Project must be provided" ) );

        final Branch mediaPathBranch = params.getBranch() != null
            ? Branch.from( params.getBranch() )
            : Objects.requireNonNullElse( portalRequest.getBranch(), ContentConstants.BRANCH_MASTER );

        final ProjectName prefixAndBaseUrlProjectName = portalRequest.getRepositoryId() != null
            ? ProjectName.from( portalRequest.getRepositoryId() )
            : ProjectName.from( params.getProjectName() );

        final Branch prefixAndBaseUrlBranch = portalRequest.getBranch() != null
            ? portalRequest.getBranch()
            : Branch.from( Objects.requireNonNullElse( params.getBranch(), ContentConstants.BRANCH_MASTER.getValue() ) );

        final Media media = ContextBuilder.copyOf( ContextAccessor.current() )
            .repositoryId( mediaPathProjectName.getRepoId() )
            .branch( mediaPathBranch )
            .build()
            .callWith( () -> getMedia( Objects.requireNonNullElse( params.getId(), params.getPath() ) ) );

        final Site site = ContextBuilder.copyOf( ContextAccessor.current() )
            .repositoryId( prefixAndBaseUrlProjectName.getRepoId() )
            .branch( prefixAndBaseUrlBranch )
            .build()
            .callWith( () -> {
                final ContentResolver contentResolver = new ContentResolver( contentService );
                return contentResolver.resolve( portalRequest ).getNearestSite();
            } );

        final BaseUrlStrategy baseUrlStrategy = requestBaseUrlStrategy( portalRequest, params.getType() );

        final PathPrefixStrategy pathPrefixStrategy = () -> {
            final StringBuilder prefix = new StringBuilder();

            appendPart( prefix, portalRequest.getBaseUri() );
            if ( portalRequest.isSiteBase() )
            {
                appendPart( prefix, "site" );
                appendPart( prefix, prefixAndBaseUrlProjectName.toString() );
                appendPart( prefix, prefixAndBaseUrlBranch.getValue() );
                if ( site != null )
                {
                    appendPart( prefix, site.getPath().toString() );
                }
            }
            appendPart( prefix, "_" );

            return prefix.toString();
        };

        final RewritePathStrategy rewritePathStrategy = requestRewriteStrategy( portalRequest );

        final ImageUrlGeneratorParams generatorParams = new ImageUrlGeneratorParams();

        generatorParams.baseUrlStrategy = baseUrlStrategy;
        generatorParams.pathPrefixStrategy = pathPrefixStrategy;
        generatorParams.rewritePathStrategy = rewritePathStrategy;

        generatorParams.mediaProvider = () -> media;

        generatorParams.projectName = mediaPathProjectName;
        generatorParams.branch = mediaPathBranch;

        generatorParams.scale = params.getScale();
        generatorParams.format = params.getFormat();
        generatorParams.filter = params.getFilter();
        generatorParams.quality = params.getQuality();
        generatorParams.background = params.getBackground();

        return generatorParams;
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
