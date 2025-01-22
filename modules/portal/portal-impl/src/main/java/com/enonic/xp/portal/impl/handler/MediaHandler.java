package com.enonic.xp.portal.impl.handler;

import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Media;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.exception.ThrottlingException;
import com.enonic.xp.image.ImageService;
import com.enonic.xp.image.ReadImageParams;
import com.enonic.xp.image.ScaleParams;
import com.enonic.xp.image.ScaleParamsParser;
import com.enonic.xp.media.ImageOrientation;
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
import com.enonic.xp.util.MediaTypes;
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
        "^/(_|api)/media/(?<mediaType>image|attachment)/(?<context>(?<project>[^/:]+)(?::(?<branch>draft))?)/(?<id>[^/:]+)(?::(?<fingerprint>[^/]+))?/(?<restPath>.*)$" );

    private static final Pattern ATTACHMENT_REST_PATH_PATTERN = Pattern.compile( "^(?<name>[^/?]+)(\\?(?<params>.*))?$" );

    private static final Pattern IMAGE_REST_PATH_PATTERN = Pattern.compile( "^(?<scaleParams>[^/]+)/(?<name>[^/]+)$" );

    private static final Pattern MEDIA_SCOPE_DELIMITER_PATTERN = Pattern.compile( "," );

    private static final EnumSet<HttpMethod> ALLOWED_METHODS = EnumSet.of( HttpMethod.GET, HttpMethod.HEAD, HttpMethod.OPTIONS );

    private static final Predicate<PortalRequest> IS_ALLOWED_METHOD = req -> ALLOWED_METHODS.contains( req.getMethod() );

    private static final MediaType SVG_MEDIA_TYPE = MediaType.SVG_UTF_8.withoutParameters();

    private static final int DEFAULT_BACKGROUND = 0xFFFFFF;

    private static final int DEFAULT_QUALITY = 85;

    private final ContentService contentService;

    private final ImageService imageService;

    private final MediaInfoService mediaInfoService;

    private volatile String privateCacheControlHeaderConfig;

    private volatile String publicCacheControlHeaderConfig;

    private volatile String contentSecurityPolicy;

    private volatile String contentSecurityPolicySvg;

    private volatile List<PrincipalKey> draftBranchAllowedFor;

    @Activate
    public MediaHandler( @Reference final ContentService contentService, @Reference final ImageService imageService,
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

    public WebResponse handle( final WebRequest webRequest )
        throws Exception
    {
        final PortalRequest portalRequest =
            webRequest instanceof PortalRequest ? (PortalRequest) webRequest : new PortalRequest( webRequest );

        final Matcher matcher =
            PATTERN.matcher( Objects.requireNonNullElse( portalRequest.getEndpointPath(), portalRequest.getRawPath() ) );
        if ( !matcher.matches() )
        {
            throw createNotFoundException();
        }

        if ( !IS_ALLOWED_METHOD.test( portalRequest ) )
        {
            throw new WebException( HttpStatus.METHOD_NOT_ALLOWED, String.format( "Method %s not allowed", portalRequest.getMethod() ) );
        }

        if ( portalRequest.getMethod() == HttpMethod.OPTIONS )
        {
            return HandlerHelper.handleDefaultOptions( ALLOWED_METHODS );
        }

        if ( !( portalRequest.isSiteBase() || portalRequest.getRawPath().startsWith( "/api/" ) ) )
        {
            throw createNotFoundException();
        }

        if ( portalRequest.isSiteBase() )
        {
            final ContentResolverResult contentResolverResult = new ContentResolver( contentService ).resolve( portalRequest );
            if ( !"/".equals( contentResolverResult.getSiteRelativePath() ) )
            {
                throw createNotFoundException();
            }
        }

        final RepositoryId repositoryId =
            HandlerHelper.resolveRepositoryId( ProjectConstants.PROJECT_REPO_ID_PREFIX + matcher.group( "project" ) );
        final Branch branch = HandlerHelper.resolveBranch( Objects.requireNonNullElse( matcher.group( "branch" ), "master" ) );
        final String type = matcher.group( "mediaType" );
        final ContentId id = ContentId.from( matcher.group( "id" ) );
        final String fingerprint = matcher.group( "fingerprint" );
        final String restPath = matcher.group( "restPath" );

        verifyMediaScope( matcher.group( "context" ), repositoryId, branch, portalRequest );
        verifyAccessByBranch( branch );

        portalRequest.setRepositoryId( repositoryId );
        portalRequest.setBranch( branch );

        return executeInContext( repositoryId, branch, () -> type.equals( "attachment" )
            ? doHandleAttachment( portalRequest, id, fingerprint, restPath )
            : doHandleImage( portalRequest, id, fingerprint, restPath ) );
    }

    private void verifyMediaScope( final String projectContext, final RepositoryId repositoryId, final Branch branch,
                                   final PortalRequest portalRequest )
    {
        final String mediaServiceScope = VirtualHostContextHelper.getMediaServiceScope();
        if ( mediaServiceScope != null )
        {
            if ( MEDIA_SCOPE_DELIMITER_PATTERN.splitAsStream( mediaServiceScope ).map( String::trim ).noneMatch( projectContext::equals ) )
            {
                throw createNotFoundException();
            }
        }

        if ( portalRequest.getRawPath().startsWith( "/api/" ) )
        {
            return;
        }

        if ( !( repositoryId.equals( portalRequest.getRepositoryId() ) && branch.equals( portalRequest.getBranch() ) ) )
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

    private PortalResponse doHandleImage( final PortalRequest portalRequest, final ContentId id, final String fingerprint,
                                          final String restPath )
        throws Exception
    {
        final Matcher matcher = IMAGE_REST_PATH_PATTERN.matcher( restPath );
        if ( !matcher.matches() )
        {
            throw createNotFoundException();
        }

        final Content content = getContent( id );
        if ( !( content instanceof final Media media ) || !media.isImage() )
        {
            throw WebException.notFound( String.format( "Content with id [%s] is not an Image", content.getId() ) );
        }

        final Attachment attachment = media.getMediaAttachment();
        if ( attachment == null )
        {
            throw WebException.notFound( String.format( "Attachment [%s] not found", content.getName() ) );
        }

        return resolveMedia( attachment, media, fingerprint, portalRequest, matcher, false );
    }

    private PortalResponse doHandleAttachment( final PortalRequest portalRequest, final ContentId id, final String fingerprint,
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

        return resolveMedia( attachment, content, fingerprint, portalRequest, matcher, true );
    }

    private PortalResponse resolveMedia( final Attachment attachment, final Content content, final String fingerprint,
                                         final PortalRequest portalRequest, final Matcher matcher, final boolean isAttachment )
        throws Exception
    {
        final BinaryReference binaryReference = attachment.getBinaryReference();
        final ByteSource binary = getBinary( content.getId(), binaryReference );
        final MediaType attachmentMimeType = MediaType.parse( attachment.getMimeType() );
        final boolean isSvgz = "svgz".equals( attachment.getExtension() );
        final boolean isGif = attachmentMimeType.is( MediaType.GIF );
        final String name = matcher.group( "name" );

        final MediaType contentType;
        if ( !isAttachment && !content.getName().toString().equals( name ) )
        {
            if ( !content.getName().toString().equals( Files.getNameWithoutExtension( name ) ) )
            {
                throw WebException.notFound( String.format( "Image [%s] not found for content [%s]", name, content.getId() ) );
            }
            contentType = MediaTypes.instance().fromFile( name );
        }
        else
        {
            contentType = resolveContentType( isSvgz, isGif, attachmentMimeType );
        }

        final ByteSource body;
        if ( isAttachment || isGif || isSvgz || attachmentMimeType.is( SVG_MEDIA_TYPE ) )
        {
            body = binary;
        }
        else
        {
            final ScaleParams scaleParams = new ScaleParamsParser().parse( matcher.group( "scaleParams" ) );
            body = transform( (Media) content, binaryReference, binary, contentType, scaleParams, portalRequest );
        }

        final Trace trace = Tracer.current();
        if ( trace != null )
        {
            trace.put( "contentPath", content.getPath() );
            trace.put( "type", isAttachment ? "attachment" : "image" );
        }

        final PortalResponse.Builder portalResponse = PortalResponse.create();

        setContentEncodingHeader( portalResponse, isSvgz );
        setContentSecurityPolicy( portalResponse, contentType );
        setCacheControlHeader( portalResponse, content, fingerprint, portalRequest, attachment );

        if ( isAttachment )
        {
            setDispositionHeader( portalResponse, portalRequest, attachment.getName() );
            new RangeRequestHelper().handleRangeRequest( portalRequest, portalResponse, body, contentType );
        }
        else
        {
            portalResponse.contentType( contentType );
            portalResponse.body( body );
        }

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
                                        final PortalRequest portalRequest, final Attachment attachment )
    {
        if ( !nullToEmpty( fingerprint ).isBlank() )
        {
            final boolean isPublic = content.getPermissions().isAllowedFor( RoleKeys.EVERYONE, Permission.READ ) &&
                ContentConstants.BRANCH_MASTER.equals( portalRequest.getBranch() );
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

    private void setDispositionHeader( final PortalResponse.Builder portalResponse, final PortalRequest portalRequest, final String name )
    {
        if ( HandlerHelper.getParameter( portalRequest, "download" ) != null )
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

    private ByteSource transform( final Media content, final BinaryReference binaryReference, final ByteSource binary,
                                  final MediaType contentType, final ScaleParams scaleParams, final PortalRequest portalRequest )
        throws IOException
    {
        final String qualityParam = HandlerHelper.getParameter( portalRequest, "quality" );
        final String backgroundParam = HandlerHelper.getParameter( portalRequest, "background" );
        final String filterParam = HandlerHelper.getParameter( portalRequest, "filter" );

        final ImageOrientation imageOrientation = Objects.requireNonNullElseGet( content.getOrientation(), () -> Objects.requireNonNullElse(
            mediaInfoService.getImageOrientation( binary ), ImageOrientation.TopLeft ) );

        final int imageQuality = nullToEmpty( qualityParam ).isEmpty() ? DEFAULT_QUALITY : Integer.parseInt( qualityParam );

        final int backgroundColor = nullToEmpty( backgroundParam ).isEmpty()
            ? DEFAULT_BACKGROUND
            : Integer.parseInt( backgroundParam.startsWith( "0x" ) ? backgroundParam.substring( 2 ) : backgroundParam, 16 );
        try
        {
            final ReadImageParams readImageParams = ReadImageParams.newImageParams()
                .contentId( content.getId() )
                .binaryReference( binaryReference )
                .cropping( content.getCropping() )
                .focalPoint( content.getFocalPoint() )
                .orientation( imageOrientation )
                .scaleParams( scaleParams )
                .filterParam( filterParam )
                .backgroundColor( backgroundColor )
                .quality( imageQuality )
                .mimeType( contentType.toString() )
                .build();

            return this.imageService.readImage( readImageParams );
        }
        catch ( IllegalArgumentException e )
        {
            throw new WebException( HttpStatus.BAD_REQUEST, "Invalid parameters", e );
        }
        catch ( ThrottlingException e )
        {
            throw new WebException( HttpStatus.TOO_MANY_REQUESTS, "Try again later", e );
        }
    }

    private WebException createNotFoundException()
    {
        return WebException.notFound( "Not a valid media url pattern" );
    }
}
