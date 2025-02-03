package com.enonic.xp.portal.impl.url3;

import java.util.Objects;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

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
import com.enonic.xp.portal.url.BaseUrlStrategy;
import com.enonic.xp.portal.url.ImageMediaUrlParams;
import com.enonic.xp.portal.url.PathPrefixStrategy;
import com.enonic.xp.portal.url.RewritePathStrategy;
import com.enonic.xp.project.ProjectName;

@Component(immediate = true, service = MediaService.class)
public class MediaService
{
    private final ContentService contentService;

    private final MediaPathPrefixStrategyFactory mediaPathPrefixStrategyFactory;

    private final BaseUrlStrategyFactory baseUrlStrategyFactory;

    @Activate
    public MediaService( @Reference final ContentService contentService,
                         @Reference final MediaPathPrefixStrategyFactory mediaPathPrefixStrategyFactory,
                         @Reference final BaseUrlStrategyFactory baseUrlStrategyFactory )
    {
        this.contentService = contentService;
        this.mediaPathPrefixStrategyFactory = mediaPathPrefixStrategyFactory;
        this.baseUrlStrategyFactory = baseUrlStrategyFactory;
    }

//    public static PathPrefixStrategy harmonized( PortalRequest portalRequest )
//    {
//        return new HarmonizedApiPathPrefixStrategy( contentService, HarmonizedApiPathPrefixStrategyParams.create()
//            .setPortalRequest( portalRequest )
//            .build() );
//    }
//
//    public static PathPrefixStrategy slashApi( String baseUri )
//    {
//        return () -> baseUri;
//    }
//
//    public static RewritePathStrategy rewriteRequest( PortalRequest portalRequest )
//    {
//        return new RequestRewritePathStrategy( portalRequest );
//    }
//
//    public static RewritePathStrategy doNotRewrite()
//    {
//        return path -> path;
//    }
//
//    public interface GetNearestSiteStrategy
//    {
//        Site getNearestSite();
//    }
//
//    public static GetNearestSiteStrategy predefinedNearestSite( Site site )
//    {
//        return () -> site;
//    }
//
//    public static GetNearestSiteStrategy getNearestSite( PortalRequest portalRequest )
//    {
//        return () -> null; // From content resolver
//    }
//
//    public class UrlGeneratorParams
//    {
//        private final PathPrefixStrategy pathPrefixStrategy;
//
//        private final BaseUrlStrategy baseUrlStrategy;
//
//        private final RewritePathStrategy rewritePathStrategy;
//
//        private final GetNearestSiteStrategy getNearestSiteStrategy;
//
//        private UrlGeneratorParams( final PathPrefixStrategy pathPrefixStrategy, final BaseUrlStrategy baseUrlStrategy,
//                                    final RewritePathStrategy rewritePathStrategy, final GetNearestSiteStrategy getNearestSiteStrategy )
//        {
//            this.pathPrefixStrategy = pathPrefixStrategy;
//            this.baseUrlStrategy = baseUrlStrategy;
//            this.rewritePathStrategy = rewritePathStrategy;
//            this.getNearestSiteStrategy = getNearestSiteStrategy;
//        }
//    }
//
//    public String siteRequestImageMediaUrl( PortalRequest r, String p, String b, String f )
//    {
////        String project = Objects.requireNonNullElse( p, r.getRepositoryId() );
////        PathPrefixStrategy pathPrefixStrategy = harmonized( r );
////        BaseUrlStrategy baseUrlStrategy = new RequestBaseUrlStrategy( r, UrlTypeConstants.SERVER_RELATIVE );
//        return null;
//    }

    public String imageMediaUrl( final ImageMediaUrlParams params )
    {
        final PathPrefixStrategy pathPrefixStrategy = mediaPathPrefixStrategyFactory.create( params );
        final BaseUrlStrategy baseUrlStrategy = baseUrlStrategyFactory.create( params );
        final RewritePathStrategy rewritePathStrategy = RewritePathStrategyFactory.mediaRewriteStrategy( params.getWebRequest() );

        final ProjectName projectName = getProjectName( params );
        final Branch branch = getBranch( params );

        final Context context =
            ContextBuilder.copyOf( ContextAccessor.current() ).repositoryId( projectName.getRepoId() ).branch( branch ).build();

        final Media media =
            context.callWith( () -> getMedia( Objects.requireNonNullElse( params.getContentId(), params.getContentPath() ) ) );

        ////////////

        final ImageMediaPathStrategyParams imageMediaPathStrategyParams = ImageMediaPathStrategyParams.create()
            .setMedia( media )
            .setProjectName( projectName )
            .setBranch( branch )
            .setScale( params.getScale() )
            .build();

        final MediaPathStrategy mediaPathStrategy =
            new MediaPathStrategy( pathPrefixStrategy, new ImageMediaPathStrategy( imageMediaPathStrategyParams ) );

        return UrlGenerator.generateUrl( baseUrlStrategy, mediaPathStrategy, rewritePathStrategy );
    }

    private ProjectName getProjectName( final ImageMediaUrlParams params )
    {
        if ( params.getWebRequest() instanceof PortalRequest portalRequest )
        {
            if ( !portalRequest.isSiteBase() )
            {
                throw new IllegalArgumentException( "PortalRequest must be site based" );
            }
            return ProjectName.from( portalRequest.getRepositoryId() );
        }
        else
        {
            return ProjectName.from( Objects.requireNonNull( params.getProjectName() ) );
        }
    }

    private Branch getBranch( final ImageMediaUrlParams params )
    {
        if ( params.getWebRequest() instanceof PortalRequest portalRequest )
        {
            if ( !portalRequest.isSiteBase() )
            {
                throw new IllegalArgumentException( "PortalRequest must be site based" );
            }
            return portalRequest.getBranch();
        }
        else
        {
            return Branch.from( Objects.requireNonNullElse( params.getBranch(), "master" ) );
        }
    }

    private Media getMedia( final String contentKey )
    {
        Content content;
        if ( contentKey.startsWith( "/" ) )
        {
            content = contentService.getByPath( ContentPath.from( contentKey ) );
        }
        else
        {
            content = contentService.getById( ContentId.from( contentKey ) );
        }

        if ( !( content instanceof Media ) )
        {
            throw new ContentInNotMediaException( String.format( "Content [%s] is not a Media", contentKey ) );
        }

        return (Media) content;
    }

    private static class ContentInNotMediaException
        extends NotFoundException
    {
        ContentInNotMediaException( final String message )
        {
            super( message );
        }
    }

}
