package com.enonic.xp.portal.impl.url;

import java.util.Objects;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.impl.ContentResolverResult;
import com.enonic.xp.portal.url.AttachmentMediaUrlParams;
import com.enonic.xp.repository.RepositoryUtils;

import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendParams;
import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendPart;

public class AttachmentMediaUrlGenerator
    extends BaseUrlGenerator<AttachmentMediaUrlParams>
{
    public AttachmentMediaUrlGenerator( final ContentService contentService )
    {
        super( contentService );
    }

    @Override
    public String doGenerateUrl( final AttachmentMediaUrlParams params )
    {
        final StringBuilder url = new StringBuilder();

        final Content content = resolveContent( params );
        final String projectName = resolveProjectName( params );
        final String branch = resolveBranch( params );

        if ( params.getWebRequest() instanceof PortalRequest portalRequest )
        {
            final ContentResolverResult contentResolverResult =
                new com.enonic.xp.portal.impl.ContentResolver( contentService ).resolve( portalRequest );

            if ( contentResolverResult.getNearestSite() != null )
            {
                appendPart( url, portalRequest.getBaseUri() );
                appendPart( url, contentResolverResult.getNearestSite().getPath().toString() );
            }
            else
            {
                appendPart( url, "site" );
                appendPart( url, projectName );
                appendPart( url, branch );
            }

            appendPart( url, "_" );
        }
        else
        {
            appendPart( url, "api" );
        }

        appendPart( url, "media" );
        appendPart( url, "attachment" );
        appendPart( url, ContentConstants.BRANCH_DRAFT.getValue().equals( branch ) ? projectName + ":" + branch : projectName );

        final Attachment attachment = resolveAttachment( content, params );
        final String hash = resolveHash( attachment );

        appendPart( url, content.getId().toString() + ( hash != null ? ":" + hash : "" ) );
        appendPart( url, attachment.getName() );

        final Multimap<String, String> queryParams = LinkedListMultimap.create();
        if ( params.isDownload() )
        {
            queryParams.put( "download", null );
        }
        queryParams.putAll( params.getQueryParams() );

        appendParams( url, queryParams.entries() );

        return url.toString();
    }

    // TODO Should we check repoId and branch via ContextAccessor?
    private String resolveProjectName( final AttachmentMediaUrlParams params )
    {
        if ( params.getWebRequest() instanceof PortalRequest portalRequest )
        {
            return RepositoryUtils.getContentRepoName( portalRequest.getRepositoryId() );
        }
        return Objects.requireNonNull( params.getProjectName() );
    }

    private String resolveBranch( final AttachmentMediaUrlParams params )
    {
        if ( params.getWebRequest() instanceof PortalRequest portalRequest )
        {
            return portalRequest.getBranch().getValue();
        }
        return Objects.requireNonNullElse( params.getBranch(), "master" );
    }

    private Content resolveContent( final AttachmentMediaUrlParams params )
    {
        if ( params.getWebRequest() instanceof PortalRequest portalRequest )
        {
            return new ContentResolver().portalRequest( portalRequest )
                .contentService( this.contentService )
                .id( params.getContentId() )
                .path( params.getContentPath() )
                .resolve();
        }

        if ( params.getContentId() == null || params.getContentPath() == null )
        {
            throw new IllegalArgumentException( "ContentId or ContentPath must be set" );
        }

        if ( params.getContentId() != null )
        {
            return contentService.getById( ContentId.from( params.getContentId() ) );
        }

        return contentService.getByPath( ContentPath.from( params.getContentPath() ) );
    }

    private Attachment resolveAttachment( final Content content, final AttachmentMediaUrlParams params )
    {
        final Attachments attachments = content.getAttachments();

        final String attachmentName =
            Objects.requireNonNullElseGet( params.getName(), () -> Objects.requireNonNullElse( params.getLabel(), "source" ) );

        try
        {
            return Objects.requireNonNullElseGet( attachments.byName( attachmentName ), () -> attachments.byLabel( attachmentName ) );
        }
        catch ( NullPointerException e )
        {
            throw new IllegalArgumentException(
                String.format( "Could not find attachment with name/label [%s] on content [%s]", attachmentName, content.getId() ), e );
        }
    }

    private String resolveHash( final Attachment attachment )
    {
        return attachment.getSha512() != null ? attachment.getSha512().substring( 0, 32 ) : null;
    }

}
