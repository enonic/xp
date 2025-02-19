package com.enonic.xp.portal.impl.handler;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.image.ImageService;
import com.enonic.xp.image.ScaleParamsParser;
import com.enonic.xp.media.MediaInfoService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.impl.ContentResolver;
import com.enonic.xp.portal.impl.ContentResolverResult;
import com.enonic.xp.portal.impl.PortalConfig;
import com.enonic.xp.portal.impl.VirtualHostContextHelper;
import com.enonic.xp.portal.impl.handler.image.ImageHandlerWorker;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

import static com.google.common.base.Strings.nullToEmpty;

@Component(immediate = true, service = UniversalApiHandler.class, property = {"applicationKey=media", "apiKey=image",
    "displayName=Image Media API", "allowedPrincipals=role:system.everyone", "mount=true"}, configurationPid = "com.enonic.xp.portal")
public class ImageMediaHandler
    implements UniversalApiHandler
{
    private static final String API_HANDLER_KEY = "/media:image/";

    private static final EnumSet<HttpMethod> ALLOWED_METHODS = EnumSet.of( HttpMethod.GET, HttpMethod.HEAD, HttpMethod.OPTIONS );

    private final ContentService contentService;

    private final ImageService imageService;

    private final MediaInfoService mediaInfoService;

    private volatile String privateCacheControlHeaderConfig;

    private volatile String publicCacheControlHeaderConfig;

    private volatile String contentSecurityPolicy;

    private volatile String contentSecurityPolicySvg;

    private volatile List<PrincipalKey> draftBranchAllowedFor;

    @Activate
    public ImageMediaHandler( @Reference final ContentService contentService, @Reference final ImageService imageService,
                              @Reference final MediaInfoService mediaInfoService )
    {
        this.contentService = contentService;
        this.imageService = imageService;
        this.mediaInfoService = mediaInfoService;
    }

    @Activate
    @Modified
    public void activate( final PortalConfig config )
    {
        this.privateCacheControlHeaderConfig = config.media_private_cacheControl();
        this.publicCacheControlHeaderConfig = config.media_public_cacheControl();
        this.contentSecurityPolicy = config.media_contentSecurityPolicy();
        this.contentSecurityPolicySvg = config.media_contentSecurityPolicy_svg();
        this.draftBranchAllowedFor = Arrays.stream( nullToEmpty( config.draftBranchAllowedFor() ).split( ",", -1 ) )
            .map( String::trim )
            .map( PrincipalKey::from )
            .collect( Collectors.toList() );
    }

    @Override
    public WebResponse handle( final WebRequest webRequest )
    {
        final PortalRequest portalRequest = (PortalRequest) webRequest;

        final String path = Objects.requireNonNullElse( portalRequest.getEndpointPath(), portalRequest.getRawPath() );
        final PathParser pathParser = new PathParser( path );
        final PathMetadata pathMetadata = pathParser.parse();

        if ( portalRequest.getMethod() == HttpMethod.OPTIONS )
        {
            return HandlerHelper.handleDefaultOptions( ALLOWED_METHODS );
        }

        checkArguments( portalRequest, pathMetadata );

        return ContextBuilder.copyOf( ContextAccessor.current() )
            .repositoryId( pathMetadata.repositoryId )
            .branch( pathMetadata.branch )
            .build()
            .callWith( () -> {
                final ImageHandlerWorker worker =
                    new ImageHandlerWorker( portalRequest, this.contentService, this.imageService, this.mediaInfoService );

                worker.id = pathMetadata.contentId;
                worker.fingerprint = pathMetadata.fingerprint;
                worker.scaleParams = new ScaleParamsParser().parse( pathMetadata.scaleParams );
                worker.name = pathMetadata.name;
                worker.filterParam = HandlerHelper.getParameter( portalRequest, "filter" );
                worker.qualityParam = HandlerHelper.getParameter( portalRequest, "quality" );
                worker.backgroundParam = HandlerHelper.getParameter( portalRequest, "background" );
                worker.privateCacheControlHeaderConfig = this.privateCacheControlHeaderConfig;
                worker.publicCacheControlHeaderConfig = this.publicCacheControlHeaderConfig;
                worker.contentSecurityPolicy = this.contentSecurityPolicy;
                worker.contentSecurityPolicySvg = this.contentSecurityPolicySvg;
                worker.legacyMode = false;
                worker.branch = pathMetadata.branch;

                return worker.execute();
            } );
    }

    private void checkArguments( final PortalRequest portalRequest, final PathMetadata pathMetadata )
    {
        if ( !ALLOWED_METHODS.contains( portalRequest.getMethod() ) )
        {
            throw new WebException( HttpStatus.METHOD_NOT_ALLOWED, String.format( "Method %s not allowed", portalRequest.getMethod() ) );
        }

        if ( portalRequest.getEndpointPath() != null && portalRequest.isSiteBase() )
        {
            final ContentResolverResult contentResolverResult = new ContentResolver( contentService ).resolve( portalRequest );
            if ( !"/".equals( contentResolverResult.getSiteRelativePath() ) )
            {
                throw createNotFoundException();
            }
        }

        final String mediaServiceScope = VirtualHostContextHelper.getMediaServiceScope();
        if ( mediaServiceScope != null &&
            Arrays.stream( mediaServiceScope.split( ",", -1 ) ).map( String::trim ).noneMatch( pathMetadata.context::equals ) )
        {
            throw createNotFoundException();
        }

        // TODO Should we check this the same way as it is done in SiteHandler?
//        if ( ContentConstants.BRANCH_DRAFT.equals( pathMetadata.branch ) )
//        {
//            final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
//            if ( !authInfo.hasRole( RoleKeys.ADMIN ) && authInfo.getPrincipals().stream().noneMatch( draftBranchAllowedFor::contains ) )
//            {
//                throw WebException.forbidden( "You don't have permission to access this resource" );
//            }
//        }
    }

    private static WebException createNotFoundException()
    {
        return WebException.notFound( "Not a valid media url pattern" );
    }

    private static final class PathMetadata
    {
        String context;

        RepositoryId repositoryId;

        Branch branch;

        ContentId contentId;

        String fingerprint;

        String scaleParams;

        String name;
    }

    private static final class PathParser
    {
        static final int CONTEXT_INDEX = 0;

        static final int CONTEXT_PROJECT_INDEX = 0;

        static final int CONTEXT_BRANCH_INDEX = 1;

        static final int IDENTIFIER_INDEX = 1;

        static final int IDENTIFIER_CONTENT_ID_INDEX = 0;

        static final int IDENTIFIER_FINGERPRINT_INDEX = 1;

        static final int SCALE_INDEX = 2;

        static final int NAME_INDEX = 3;

        private final String[] pathVariables;

        PathParser( final String path )
        {
            int pos = Objects.requireNonNull( path ).indexOf( API_HANDLER_KEY );
            if ( pos == -1 )
            {
                throw createNotFoundException();
            }

            // Limit is 5 to handle the case when the path ends with a slash, but we must have exactly 4 path variables to resolve the media
            String[] pathVariables = path.substring( pos + API_HANDLER_KEY.length() ).split( "/", 5 );

            if ( pathVariables.length < 4 )
            {
                throw createNotFoundException();
            }

            this.pathVariables = pathVariables;
        }

        PathMetadata parse()
        {
            final PathMetadata metadata = new PathMetadata();

            metadata.context = pathVariables[CONTEXT_INDEX];

            String[] contextParts = metadata.context.split( ":", 2 );
            metadata.repositoryId = HandlerHelper.resolveProjectName( contextParts[CONTEXT_PROJECT_INDEX] ).getRepoId();
            metadata.branch = HandlerHelper.resolveBranch( contextParts.length > 1 ? contextParts[CONTEXT_BRANCH_INDEX] : "master" );

            String[] identifierPathVariable = pathVariables[IDENTIFIER_INDEX].split( ":", 2 );
            metadata.contentId = ContentId.from( identifierPathVariable[IDENTIFIER_CONTENT_ID_INDEX] );
            metadata.fingerprint = identifierPathVariable.length > 1 ? identifierPathVariable[IDENTIFIER_FINGERPRINT_INDEX] : null;

            metadata.scaleParams = pathVariables[SCALE_INDEX];
            metadata.name = pathVariables[NAME_INDEX];

            return metadata;
        }
    }
}
