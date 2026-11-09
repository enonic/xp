package com.enonic.xp.portal.impl.handler;

import java.io.IOException;
import java.util.Optional;

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
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.ImageScaling;
import com.enonic.xp.portal.impl.handler.attachment.EntityTag;
import com.enonic.xp.portal.impl.handler.attachment.RangeRequestHelper;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.util.MediaTypes;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;

import static com.enonic.xp.web.servlet.ServletRequestUrlHelper.contentDispositionAttachment;
import static com.google.common.base.Strings.nullToEmpty;

public abstract class AbstractAttachmentHandlerWorker<T extends Content>
{
    private static final MediaType SVG_MEDIA_TYPE = MediaType.SVG_UTF_8.withoutParameters();

    protected ContentService contentService;

    protected WebRequest request;

    public ContentId id;

    public String name;

    public boolean download;

    public String fingerprint;

    public String privateCacheControlHeaderConfig;

    public String publicCacheControlHeaderConfig;

    public String contentSecurityPolicy;

    public String contentSecurityPolicySvg;

    public boolean legacyMode;

    public Branch branch;

    public AbstractAttachmentHandlerWorker( final WebRequest request, final ContentService contentService )
    {
        this.contentService = contentService;
        this.request = request;
    }

    public PortalResponse execute()
        throws IOException
    {
        final T content = cast( getContent( this.id ) );
        final Attachment attachment = resolveAttachment( content, this.name );
        final BinaryReference binaryReference = attachment.getBinaryReference();

        final boolean isSvgz = "svgz".equals( attachment.getExtension() );

        final MediaType attachmentMimeType = ImageScaling.mimeType( attachment );
        final boolean scalable = ImageScaling.isScalable( attachmentMimeType );

        final MediaType contentType =
            scalable && shouldConvert( content, this.name ) ? MediaTypes.instance().fromFile( this.name ) : attachmentMimeType;

        final Optional<EntityTag> entityTag = Optional.ofNullable( resolveHash( content, attachment, binaryReference ) ).map( EntityTag::new );

        final PortalResponse.Builder portalResponse = PortalResponse.create();

        entityTag.ifPresent( tag -> portalResponse.header( HttpHeaders.ETAG, tag.toHeaderValue() ) );

        if ( !nullToEmpty( this.fingerprint ).isBlank() )
        {
            final boolean isPublic = content.getPermissions().isAllowedFor( RoleKeys.EVERYONE, Permission.READ ) &&
                ContentConstants.BRANCH_MASTER.equals( branch );
            final String cacheControlHeaderConfig = isPublic ? publicCacheControlHeaderConfig : privateCacheControlHeaderConfig;

            if ( !nullToEmpty( cacheControlHeaderConfig ).isBlank() )
            {
                // fallback to private cache control header if hash is not known
                if ( entityTag.isEmpty() && !nullToEmpty( privateCacheControlHeaderConfig ).isBlank() )
                {
                    portalResponse.header( HttpHeaders.CACHE_CONTROL, privateCacheControlHeaderConfig );
                }
                if ( entityTag.map( EntityTag::value ).filter( this.fingerprint::equals ).isPresent() )
                {
                    portalResponse.header( HttpHeaders.CACHE_CONTROL, cacheControlHeaderConfig );
                }
            }
        }

        addTrace( content );

        final BinaryTransform transform =
            scalable ? prepareTransform( content, binaryReference, contentType ) : BinaryTransform.IDENTITY;

        final boolean isSvg = contentType.is( SVG_MEDIA_TYPE );
        final String contentSecurityPolicyHeader = nullToEmpty( isSvg ? contentSecurityPolicySvg : contentSecurityPolicy );
        if ( !contentSecurityPolicyHeader.isBlank() )
        {
            portalResponse.header( HttpHeaders.CONTENT_SECURITY_POLICY, contentSecurityPolicyHeader );
        }

        if ( isNotModified( entityTag ) )
        {
            return portalResponse.status( HttpStatus.NOT_MODIFIED ).build();
        }

        if ( isSvg && isSvgz )
        {
            portalResponse.header( HttpHeaders.CONTENT_ENCODING, "gzip" );
        }

        if ( download )
        {
            portalResponse.header( HttpHeaders.CONTENT_DISPOSITION, contentDispositionAttachment( attachment.getName() ) );
        }

        final ByteSource body = transform.apply( getBinary( this.id, binaryReference ) );

        writeResponseContent( portalResponse, contentType, body, entityTag );

        return portalResponse.build();
    }

    private boolean isNotModified( final Optional<EntityTag> entityTag )
    {
        final String ifNoneMatchHeader = this.request.getHeaders().get( HttpHeaders.IF_NONE_MATCH );
        return ifNoneMatchHeader != null && entityTag.filter( tag -> tag.matchesIfNoneMatch( ifNoneMatchHeader ) ).isPresent();
    }

    protected BinaryTransform prepareTransform( final T content, final BinaryReference binaryReference, final MediaType contentType )
    {
        return BinaryTransform.IDENTITY;
    }

    protected abstract String resolveHash( T content, Attachment attachment, BinaryReference binaryReference );

    protected Attachment resolveAttachment( final Content content, final String name )
    {
        final Attachments attachments = content.getAttachments();
        final Attachment attachment = attachments.byName( name );
        if ( attachment != null )
        {
            return attachment;
        }

        throw WebException.notFound( String.format( "Attachment [%s] not found for [%s]", name, content.getPath() ) );
    }

    protected boolean shouldConvert( final Content content, final String name )
    {
        return false;
    }

    protected void writeResponseContent( final PortalResponse.Builder portalResponse, final MediaType contentType, final ByteSource body,
                                         final Optional<EntityTag> entityTag )
        throws IOException
    {
        new RangeRequestHelper().handleRangeRequest( request, portalResponse, body, contentType, entityTag );
    }

    protected abstract T cast( Content content );

    protected abstract void addTrace( T content );

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

    private ByteSource getBinary( final ContentId id, final BinaryReference binaryReference )
    {
        final ByteSource binary = this.contentService.getBinary( id, binaryReference );
        if ( binary == null )
        {
            throw WebException.notFound( String.format( "Binary [%s] not found for [%s]", binaryReference, id ) );
        }

        return binary;
    }

    @FunctionalInterface
    protected interface BinaryTransform
    {
        BinaryTransform IDENTITY = binary -> binary;

        ByteSource apply( ByteSource binary )
            throws IOException;
    }
}
