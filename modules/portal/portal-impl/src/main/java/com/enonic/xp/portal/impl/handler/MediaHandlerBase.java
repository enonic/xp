package com.enonic.xp.portal.impl.handler;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.impl.ContentResolver;
import com.enonic.xp.portal.impl.ContentResolverResult;
import com.enonic.xp.portal.impl.PortalConfig;
import com.enonic.xp.portal.impl.PortalRequestHelper;
import com.enonic.xp.portal.impl.VirtualHostContextHelper;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;

import static com.google.common.base.Strings.nullToEmpty;

public abstract class MediaHandlerBase
    implements UniversalApiHandler
{
    protected static final EnumSet<HttpMethod> ALLOWED_METHODS = EnumSet.of( HttpMethod.GET, HttpMethod.HEAD, HttpMethod.OPTIONS );

    protected final ContentService contentService;

    protected final ProjectService projectService;

    protected volatile String privateCacheControlHeaderConfig;

    protected volatile String publicCacheControlHeaderConfig;

    protected volatile String contentSecurityPolicy;

    protected volatile String contentSecurityPolicySvg;

    protected volatile List<PrincipalKey> draftBranchAllowedFor;

    protected MediaHandlerBase( final ContentService contentService, final ProjectService projectService )
    {
        this.contentService = contentService;
        this.projectService = projectService;
    }

    protected final void doActivate( final PortalConfig config )
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

    protected void checkArguments( final WebRequest webRequest, final PathMetadata pathMetadata )
    {
        if ( !ALLOWED_METHODS.contains( webRequest.getMethod() ) )
        {
            throw new WebException( HttpStatus.METHOD_NOT_ALLOWED, String.format( "Method %s not allowed", webRequest.getMethod() ) );
        }

        if ( ContentConstants.BRANCH_DRAFT.equals( pathMetadata.branch ) )
        {
            final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
            if ( !authInfo.hasRole( RoleKeys.ADMIN ) && authInfo.getPrincipals().stream().noneMatch( draftBranchAllowedFor::contains ) )
            {
                throw WebException.forbidden( "You don't have permission to access this resource" );
            }
        }

        final String mediaServiceScope = VirtualHostContextHelper.getMediaServiceScope();
        if ( mediaServiceScope != null &&
            Arrays.stream( mediaServiceScope.split( ",", -1 ) ).map( String::trim ).noneMatch( pathMetadata.context::equals ) )
        {
            throw createNotFoundException();
        }

        if ( webRequest.getEndpointPath() != null && webRequest instanceof PortalRequest portalRequest &&
            PortalRequestHelper.isSiteBase( portalRequest ) )
        {
            final ContentResolverResult contentResolverResult =
                new ContentResolver( contentService, projectService ).resolve( portalRequest );
            if ( !"/".equals( contentResolverResult.getSiteRelativePath() ) )
            {
                throw createNotFoundException();
            }
        }
    }

    private static WebException createNotFoundException()
    {
        return WebException.notFound( "Not a valid media url pattern" );
    }

    protected static class PathMetadata
    {
        String context;

        RepositoryId repositoryId;

        Branch branch;

        ContentId contentId;

        String fingerprint;

        String name;
    }

    protected abstract static class PathParser<T extends PathMetadata>
    {
        static final int CONTEXT_INDEX = 0;

        static final int CONTEXT_PROJECT_INDEX = 0;

        static final int CONTEXT_BRANCH_INDEX = 1;

        static final int IDENTIFIER_INDEX = 1;

        static final int IDENTIFIER_CONTENT_ID_INDEX = 0;

        static final int IDENTIFIER_FINGERPRINT_INDEX = 1;

        protected final String[] pathVariables;

        PathParser( final String path, final String framedHandlerKey, final int pathVariableLimit )
        {
            int pos = Objects.requireNonNull( path ).indexOf( framedHandlerKey );
            if ( pos == -1 )
            {
                throw createNotFoundException();
            }

            // Limit is `pathVariableLimit` to handle the case when the path ends with a slash,
            // but we must have exactly `pathVariableLimit - 1` path variables to resolve the media
            String[] pathVariables = path.substring( pos + framedHandlerKey.length() ).split( "/", pathVariableLimit );

            if ( pathVariables.length < pathVariableLimit - 1 )
            {
                throw createNotFoundException();
            }

            this.pathVariables = pathVariables;
        }

        abstract T createMetadata();

        T doParse()
        {
            final T metadata = createMetadata();

            metadata.context = pathVariables[CONTEXT_INDEX];

            String[] contextParts = metadata.context.split( ":", 2 );
            metadata.repositoryId = HandlerHelper.resolveProjectName( contextParts[CONTEXT_PROJECT_INDEX] ).getRepoId();
            metadata.branch = HandlerHelper.resolveBranch( contextParts.length > 1 ? contextParts[CONTEXT_BRANCH_INDEX] : "master" );

            String[] identifierPathVariable = pathVariables[IDENTIFIER_INDEX].split( ":", 2 );
            metadata.contentId = ContentId.from( identifierPathVariable[IDENTIFIER_CONTENT_ID_INDEX] );
            metadata.fingerprint = identifierPathVariable.length > 1 ? identifierPathVariable[IDENTIFIER_FINGERPRINT_INDEX] : null;

            return metadata;
        }
    }
}
