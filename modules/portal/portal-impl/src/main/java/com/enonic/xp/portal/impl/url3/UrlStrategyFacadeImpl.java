package com.enonic.xp.portal.impl.url3;

import java.util.Objects;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Media;
import com.enonic.xp.context.Context;
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
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;

@Component(immediate = true, service = UrlStrategyFacade.class)
public class UrlStrategyFacadeImpl
    implements UrlStrategyFacade
{

    private final ContentService contentService;

    @Activate
    public UrlStrategyFacadeImpl( @Reference final ContentService contentService )
    {
        this.contentService = contentService;
    }

    @Override
    public PathPrefixStrategy requestPathPrefixStrategy( final PortalRequest portalRequest )
    {
        return new HarmonizedApiPathPrefixStrategy(
            HarmonizedApiPathPrefixStrategyParams.create().setPortalRequest( portalRequest ).build() );
    }

    @Override
    public PathPrefixStrategy contextPathPrefixStrategy( final ProjectName projectName, final Branch branch, final String contentKey )
    {

        return new HarmonizedApiPathPrefixStrategy( HarmonizedApiPathPrefixStrategyParams.create()
                                                        .setProjectName( projectName )
                                                        .setBranch( branch )
                                                        .setContentKey( contentKey )
                                                        .build() );
    }

    @Override
    public BaseUrlStrategy offlineBaseUrlStrategy( final ProjectName projectName, final Branch branch, final String siteKey )
    {
        Context context =
            ContextBuilder.copyOf( ContextAccessor.current() ).repositoryId( projectName.getRepoId() ).branch( branch ).build();

        Content content = context.callWith( () -> getContent( siteKey ) );

        if ( !( content instanceof Site site ) )
        {
            throw new NotFoundException( String.format( "Site [%s] not find", siteKey ) )
            {
            };
        }

        SiteConfig siteConfig = site.getSiteConfigs().get( ApplicationKey.from( "com.enonic.xp.site" ) );
        if ( siteConfig != null )
        {
            String baseUrl = siteConfig.getConfig().getString( "baseUrl" );
            return () -> Objects.requireNonNullElse( baseUrl, "/" );
        }
        else
        {
            return () -> "/";
        }
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
        final ProjectName mainPathProjectName = params.getProjectName() != null
            ? ProjectName.from( params.getProjectName() )
            : ProjectName.from( ContextAccessor.current().getRepositoryId() );

        final Branch mainPathBranch = params.getBranch() != null ? Branch.from( params.getBranch() ) : ContextAccessor.current().getBranch();

        final ProjectName prefixAndBaseUrlProjectName = ContextAccessor.current().getRepositoryId() != null
            ? ProjectName.from( ContextAccessor.current().getRepositoryId() )
            : ProjectName.from( params.getProjectName() );

        final Branch prefixAndBaseUrlBranch = ContextAccessor.current().getBranch() != null
            ? ContextAccessor.current().getBranch()
            : Branch.from( params.getBranch() );

        final String siteKey = params.getSiteKey();

        BaseUrlStrategy baseUrlStrategy = offlineBaseUrlStrategy( prefixAndBaseUrlProjectName, prefixAndBaseUrlBranch, siteKey );

        PathPrefixStrategy pathPrefixStrategy = contextPathPrefixStrategy( mainPathProjectName, mainPathBranch, siteKey );

        RewritePathStrategy rewritePathStrategy = doNotRewriteStrategy();

        Context context =
            ContextBuilder.copyOf( ContextAccessor.current() ).repositoryId( mainPathProjectName.getRepoId() ).branch( mainPathBranch ).build();

        ImageUrlGeneratorParams generatorParams = new ImageUrlGeneratorParams();

        generatorParams.baseUrlStrategy = baseUrlStrategy;
        generatorParams.pathPrefixStrategy = pathPrefixStrategy;
        generatorParams.rewritePathStrategy = rewritePathStrategy;
        generatorParams.mediaProvider =
            () -> context.callWith( () -> getMedia( Objects.requireNonNullElse( params.getId(), params.getPath() ) ) );
        generatorParams.nearestSiteProvider = () -> null; // TODO
        generatorParams.scale = params.getScale();
        generatorParams.format = params.getFormat();
        generatorParams.filter = params.getFilter();
        generatorParams.quality = params.getQuality();
        generatorParams.background = params.getBackground();
        generatorParams.id = params.getId();
        generatorParams.path = params.getPath();

        return generatorParams;
    }

    @Override
    public ImageUrlGeneratorParams requestImageUrlParams( final ImageUrlParams params )
    {
        final PortalRequest portalRequest = PortalRequestAccessor.get();

        BaseUrlStrategy baseUrlStrategy = requestBaseUrlStrategy( portalRequest, params.getType() );

        PathPrefixStrategy pathPrefixStrategy = requestPathPrefixStrategy( portalRequest );

        RewritePathStrategy rewritePathStrategy = requestRewriteStrategy( portalRequest );

        Context context = ContextBuilder.copyOf( ContextAccessor.current() )
            .repositoryId( portalRequest.getRepositoryId() )
            .branch( portalRequest.getBranch() )
            .build();

        ImageUrlGeneratorParams generatorParams = new ImageUrlGeneratorParams();

        generatorParams.baseUrlStrategy = baseUrlStrategy;
        generatorParams.pathPrefixStrategy = pathPrefixStrategy;
        generatorParams.rewritePathStrategy = rewritePathStrategy;
        generatorParams.mediaProvider =
            () -> context.callWith( () -> getMedia( Objects.requireNonNullElse( params.getId(), params.getPath() ) ) );
        generatorParams.nearestSiteProvider = () -> context.callWith( () -> {
            final ContentResolver contentResolver = new ContentResolver( contentService );
            return contentResolver.resolve( portalRequest ).getNearestSite();
        } );

        generatorParams.scale = params.getScale();
        generatorParams.format = params.getFormat();
        generatorParams.filter = params.getFilter();
        generatorParams.quality = params.getQuality();
        generatorParams.background = params.getBackground();
        generatorParams.id = params.getId();
        generatorParams.path = params.getPath();

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
            throw new NotFoundException( "Content [" + contentKey + "] is not a Media" )
            {
            };
        }

        return (Media) content;
    }

}
