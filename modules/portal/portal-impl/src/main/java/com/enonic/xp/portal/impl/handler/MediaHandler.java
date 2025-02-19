package com.enonic.xp.portal.impl.handler;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import com.google.common.io.ByteSource;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.image.ImageService;
import com.enonic.xp.media.MediaInfoService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.ContentResolver;
import com.enonic.xp.portal.impl.ContentResolverResult;
import com.enonic.xp.portal.impl.PortalConfig;
import com.enonic.xp.portal.impl.VirtualHostContextHelper;
import com.enonic.xp.portal.impl.handler.attachment.RangeRequestHelper;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

import static com.enonic.xp.web.servlet.ServletRequestUrlHelper.contentDispositionAttachment;
import static com.google.common.base.Strings.nullToEmpty;

@Component(service = MediaHandler.class, configurationPid = "com.enonic.xp.portal")
public class MediaHandler
{
    private static final Pattern PATTERN = Pattern.compile(
        "^/(_|api)/media/attachment/(?<context>(?<project>[^/:]+)(?::(?<branch>draft))?)/(?<id>[^/:]+)(?::(?<fingerprint>[^/]+))?/(?<restPath>.*)$" );

    private static final Pattern ATTACHMENT_REST_PATH_PATTERN = Pattern.compile( "^(?<name>[^/?]+)(\\?(?<params>.*))?$" );

    private static final Pattern MEDIA_SCOPE_DELIMITER_PATTERN = Pattern.compile( "," );

    private static final EnumSet<HttpMethod> ALLOWED_METHODS = EnumSet.of( HttpMethod.GET, HttpMethod.HEAD, HttpMethod.OPTIONS );

    private static final MediaType SVG_MEDIA_TYPE = MediaType.SVG_UTF_8.withoutParameters();

    private final ContentService contentService;

    private volatile String privateCacheControlHeaderConfig;

    private volatile String publicCacheControlHeaderConfig;

    private volatile String contentSecurityPolicy;

    private volatile String contentSecurityPolicySvg;

    private volatile List<PrincipalKey> draftBranchAllowedFor;

    @Activate
    public MediaHandler( @Reference final ContentService contentService )
    {
        this.contentService = contentService;
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

    public WebResponse handle( final WebRequest webRequest )
        throws Exception
    {
        final Matcher matcher = PATTERN.matcher( Objects.requireNonNullElse( webRequest.getEndpointPath(), webRequest.getRawPath() ) );
        if ( !matcher.matches() )
        {
            throw createNotFoundException();
        }

        if ( !ALLOWED_METHODS.contains( webRequest.getMethod() ) )
        {
            throw new WebException( HttpStatus.METHOD_NOT_ALLOWED, String.format( "Method %s not allowed", webRequest.getMethod() ) );
        }

        if ( webRequest.getMethod() == HttpMethod.OPTIONS )
        {
            return HandlerHelper.handleDefaultOptions( ALLOWED_METHODS );
        }

        if ( webRequest instanceof PortalRequest portalRequest )
        {
            if ( !portalRequest.isSiteBase() )
            {
                throw createNotFoundException();
            }

            final ContentResolverResult contentResolverResult = new ContentResolver( contentService ).resolve( portalRequest );
            if ( !"/".equals( contentResolverResult.getSiteRelativePath() ) )
            {
                throw createNotFoundException();
            }
        }

        final RepositoryId repositoryId = HandlerHelper.resolveProjectName( matcher.group( "project" ) ).getRepoId();
        final Branch branch = HandlerHelper.resolveBranch( Objects.requireNonNullElse( matcher.group( "branch" ), "master" ) );
        final ContentId id = ContentId.from( matcher.group( "id" ) );
        final String fingerprint = matcher.group( "fingerprint" );
        final String restPath = matcher.group( "restPath" );

        verifyMediaScope( matcher.group( "context" ), webRequest, repositoryId, branch );
        verifyAccessByBranch( branch );

        return executeInContext( repositoryId, branch, () -> doHandleAttachment( webRequest, id, fingerprint, restPath ) );
    }

    private void verifyMediaScope( final String projectContext, final WebRequest webRequest, final RepositoryId repositoryId,
                                   final Branch branch )
    {
        final String mediaServiceScope = VirtualHostContextHelper.getMediaServiceScope();
        if ( mediaServiceScope != null &&
            MEDIA_SCOPE_DELIMITER_PATTERN.splitAsStream( mediaServiceScope ).map( String::trim ).noneMatch( projectContext::equals ) )
        {
            throw createNotFoundException();
        }

        if ( webRequest instanceof PortalRequest portalRequest &&
            !( repositoryId.equals( portalRequest.getRepositoryId() ) && branch.equals( portalRequest.getBranch() ) ) )
        {
            throw createNotFoundException();
        }
    }

    private PortalResponse executeInContext( final RepositoryId repositoryId, final Branch branch, final Callable<PortalResponse> callable )
    {
        return ContextBuilder.copyOf( ContextAccessor.current() )
            .repositoryId( repositoryId )
            .branch( branch )
            .build()
            .callWith( callable );
    }

    private void verifyAccessByBranch( final Branch branch )
    {
        if ( ContentConstants.BRANCH_DRAFT.equals( branch ) )
        {
            final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
            if ( !authInfo.hasRole( RoleKeys.ADMIN ) && authInfo.getPrincipals().stream().noneMatch( draftBranchAllowedFor::contains ) )
            {
                throw WebException.forbidden( "You don't have permission to access this resource" );
            }
        }
    }

    private PortalResponse doHandleAttachment( final WebRequest webRequest, final ContentId id, final String fingerprint,
                                               final String restPath )
        throws Exception
    {
        final Matcher matcher = ATTACHMENT_REST_PATH_PATTERN.matcher( restPath );
        if ( !matcher.matches() )
        {
            throw WebException.notFound( "Not a valid attachment url pattern" );
        }

        final Content content = getContent( id );
        final Attachment attachment = resolveAttachment( content, matcher.group( "name" ) );

        return resolveMedia( attachment, content, fingerprint, webRequest, matcher );
    }

    private PortalResponse resolveMedia( final Attachment attachment, final Content content, final String fingerprint,
                                         final WebRequest webRequest, final Matcher matcher )
        throws Exception
    {
        final BinaryReference binaryReference = attachment.getBinaryReference();
        final ByteSource binary = getBinary( content.getId(), binaryReference );
        final MediaType attachmentMimeType = MediaType.parse( attachment.getMimeType() );
        final boolean isSvgz = "svgz".equals( attachment.getExtension() );
        final boolean isGif = attachmentMimeType.is( MediaType.GIF );
        final String name = matcher.group( "name" );

        final MediaType contentType = resolveContentType( isSvgz, isGif, attachmentMimeType );

        final ByteSource body = binary;

        final Trace trace = Tracer.current();
        if ( trace != null )
        {
            trace.put( "contentPath", content.getPath() );
            trace.put( "type", "attachment" );
        }

        final PortalResponse.Builder portalResponse = PortalResponse.create();

        setContentEncodingHeader( portalResponse, isSvgz );
        setContentSecurityPolicy( portalResponse, contentType );
        setCacheControlHeader( portalResponse, content, fingerprint, attachment );

        setDispositionHeader( portalResponse, webRequest, attachment.getName() );
        new RangeRequestHelper().handleRangeRequest( webRequest, portalResponse, body, contentType );

        return portalResponse.build();
    }

    private MediaType resolveContentType( final boolean isSvgz, final boolean isGif, final MediaType attachmentMimeType )
    {
        if ( isSvgz )
        {
            return SVG_MEDIA_TYPE;
        }
        else if ( isGif )
        {
            return MediaType.GIF;
        }
        else
        {
            return attachmentMimeType;
        }
    }

    private void setContentEncodingHeader( final PortalResponse.Builder portalResponse, final boolean isSvgz )
    {
        if ( isSvgz )
        {
            portalResponse.header( HttpHeaders.CONTENT_ENCODING, "gzip" );
        }
    }

    private void setCacheControlHeader( final PortalResponse.Builder portalResponse, final Content content, final String fingerprint,
                                        final Attachment attachment )
    {
        if ( !nullToEmpty( fingerprint ).isBlank() )
        {
            final boolean isPublic = content.getPermissions().isAllowedFor( RoleKeys.EVERYONE, Permission.READ ) &&
                ContentConstants.BRANCH_MASTER.equals( ContextAccessor.current().getBranch() );
            final String cacheControlHeaderConfig = isPublic ? publicCacheControlHeaderConfig : privateCacheControlHeaderConfig;

            if ( !nullToEmpty( cacheControlHeaderConfig ).isBlank() &&
                ( attachment.getSha512() != null && fingerprint.regionMatches( 0, attachment.getSha512(), 0, 32 ) ) )
            {
                portalResponse.header( HttpHeaders.CACHE_CONTROL, cacheControlHeaderConfig );
            }
        }
    }

    private void setContentSecurityPolicy( final PortalResponse.Builder portalResponse, final MediaType contentType )
    {
        if ( contentType.is( SVG_MEDIA_TYPE ) )
        {
            if ( !nullToEmpty( contentSecurityPolicySvg ).isBlank() )
            {
                portalResponse.header( HttpHeaders.CONTENT_SECURITY_POLICY, contentSecurityPolicySvg );
            }
        }
        else
        {
            if ( !nullToEmpty( contentSecurityPolicy ).isBlank() )
            {
                portalResponse.header( HttpHeaders.CONTENT_SECURITY_POLICY, contentSecurityPolicy );
            }
        }
    }

    private void setDispositionHeader( final PortalResponse.Builder portalResponse, final WebRequest webRequest, final String name )
    {
        if ( HandlerHelper.getParameter( webRequest, "download" ) != null )
        {
            portalResponse.header( HttpHeaders.CONTENT_DISPOSITION, contentDispositionAttachment( name ) );
        }
    }

    private Content getContent( final ContentId contentId )
    {
        try
        {
            return this.contentService.getById( contentId );
        }
        catch ( final Exception e )
        {
            if ( this.contentService.contentExists( contentId ) )
            {
                throw WebException.forbidden( String.format( "You don't have permission to access [%s]", contentId ) );
            }
            else
            {
                throw WebException.notFound( String.format( "Content with id [%s] not found", contentId.toString() ) );
            }
        }
    }

    private Attachment resolveAttachment( final Content content, final String name )
    {
        final Attachments attachments = content.getAttachments();
        final Attachment attachment = attachments.byName( name );
        if ( attachment == null )
        {
            throw WebException.notFound( String.format( "Attachment [%s] not found for [%s]", name, content.getPath() ) );
        }
        return attachment;
    }

    private ByteSource getBinary( final ContentId id, final BinaryReference binaryReference )
    {
        final ByteSource binary = this.contentService.getBinary( id, binaryReference );
        if ( binary == null )
        {
            throw WebException.notFound( String.format( "Binary [%s] not found for [%s]", binaryReference, id ) );
        }
        return binary;
    }

    private WebException createNotFoundException()
    {
        return WebException.notFound( "Not a valid media url pattern" );
    }
}
