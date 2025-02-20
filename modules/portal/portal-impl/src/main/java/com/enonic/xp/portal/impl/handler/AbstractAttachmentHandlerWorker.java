package com.enonic.xp.portal.impl.handler;

import java.io.IOException;

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
import com.enonic.xp.content.Media;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.MediaHashResolver;
import com.enonic.xp.portal.impl.handler.attachment.RangeRequestHelper;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.util.MediaTypes;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;

import static com.enonic.xp.web.servlet.ServletRequestUrlHelper.contentDispositionAttachment;
import static com.google.common.base.Strings.nullToEmpty;

public abstract class AbstractAttachmentHandlerWorker
{
    private static final MediaType SVG_MEDIA_TYPE = MediaType.SVG_UTF_8.withoutParameters();

    private static final MediaType AVIF_MEDIA_TYPE = MediaType.create( "image", "avif" );

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
        throws Exception
    {
        final Media media = castToMedia( getContent( this.id ) );
        final Attachment attachment = resolveAttachment( media, this.name );
        final BinaryReference binaryReference = attachment.getBinaryReference();
        final ByteSource binary = getBinary( this.id, binaryReference );

        final boolean isSvgz = "svgz".equals( attachment.getExtension() );

        final MediaType attachmentMimeType = isSvgz ? SVG_MEDIA_TYPE : MediaType.parse( attachment.getMimeType() );

        final MediaType contentType;
        final ByteSource body;
        if ( attachmentMimeType.is( MediaType.GIF ) || attachmentMimeType.is( AVIF_MEDIA_TYPE ) ||
            attachmentMimeType.is( MediaType.WEBP ) || attachmentMimeType.is( SVG_MEDIA_TYPE ) )
        {
            contentType = attachmentMimeType;
            body = binary;
        }
        else
        {
            contentType = shouldConvert( media, this.name ) ? MediaTypes.instance().fromFile( this.name ) : attachmentMimeType;
            body = transform( media, binaryReference, binary, contentType );
        }

        final PortalResponse.Builder portalResponse = PortalResponse.create();

        if ( contentType.is( SVG_MEDIA_TYPE ) )
        {
            if ( isSvgz )
            {
                portalResponse.header( HttpHeaders.CONTENT_ENCODING, "gzip" );
            }
            if ( !nullToEmpty( contentSecurityPolicySvg ).isBlank() )
            {
                portalResponse.header( HttpHeaders.CONTENT_SECURITY_POLICY, contentSecurityPolicySvg );
            }
        }
        else if ( !nullToEmpty( contentSecurityPolicy ).isBlank() )
        {
            portalResponse.header( HttpHeaders.CONTENT_SECURITY_POLICY, contentSecurityPolicy );
        }

        if ( !nullToEmpty( this.fingerprint ).isBlank() )
        {
            final boolean isPublic = media.getPermissions().isAllowedFor( RoleKeys.EVERYONE, Permission.READ ) &&
                ContentConstants.BRANCH_MASTER.equals( branch );
            final String cacheControlHeaderConfig = isPublic ? publicCacheControlHeaderConfig : privateCacheControlHeaderConfig;

            if ( !nullToEmpty( cacheControlHeaderConfig ).isBlank() &&
                this.fingerprint.equals( resolveHash( media, attachment, binaryReference ) ) )
            {
                portalResponse.header( HttpHeaders.CACHE_CONTROL, cacheControlHeaderConfig );
            }
        }

        if ( download )
        {
            portalResponse.header( HttpHeaders.CONTENT_DISPOSITION, contentDispositionAttachment( attachment.getName() ) );
        }

        addTrace( media );

        writeResponseContent( portalResponse, contentType, body );

        return portalResponse.build();
    }

    protected ByteSource transform( final Media content, final BinaryReference binaryReference, final ByteSource binary,
                                    final MediaType contentType )
        throws IOException
    {
        return binary;
    }

    private String resolveHash( final Media content, final Attachment attachment, final BinaryReference binaryReference )
    {
        if ( legacyMode )
        {
            final String hash = this.contentService.getBinaryKey( content.getId(), binaryReference );
            return content.isImage() ? MediaHashResolver.resolveImageHash( content, hash ) : hash;
        }
        else
        {
            final String hash = MediaHashResolver.resolveAttachmentHash( attachment );
            return content.isImage() ? MediaHashResolver.resolveImageHash( content, hash ) : hash;
        }
    }

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

    protected void writeResponseContent( final PortalResponse.Builder portalResponse, final MediaType contentType, final ByteSource body )
        throws IOException
    {
        new RangeRequestHelper().handleRangeRequest( request, portalResponse, body, contentType );
    }

    protected Media castToMedia( final Content content )
    {
        if ( content instanceof Media )
        {
            return (Media) content;
        }
        throw WebException.notFound( String.format( "Content with id [%s] is not a Media", content.getId() ) );
    }

    protected void addTrace( final Media media )
    {
        final Trace trace = Tracer.current();
        if ( trace != null )
        {
            trace.put( "contentPath", media.getPath() );
            trace.put( "type", media.isImage() ? "image" : "attachment" );
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

    private ByteSource getBinary( final ContentId id, final BinaryReference binaryReference )
    {
        final ByteSource binary = this.contentService.getBinary( id, binaryReference );
        if ( binary == null )
        {
            throw WebException.notFound( String.format( "Binary [%s] not found for [%s]", binaryReference, id ) );
        }

        return binary;
    }
}
