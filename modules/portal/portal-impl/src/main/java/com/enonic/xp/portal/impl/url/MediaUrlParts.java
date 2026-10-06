package com.enonic.xp.portal.impl.url;

import java.util.Map;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Media;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.portal.impl.ImageScaling;
import com.enonic.xp.portal.url.AttachmentUrlParts;
import com.enonic.xp.portal.url.AttachmentUrlPartsParams;
import com.enonic.xp.portal.url.ImageUrlParts;
import com.enonic.xp.portal.url.ImageUrlPartsParams;
import com.enonic.xp.project.ProjectName;

import static com.enonic.xp.portal.impl.url.PortalUrlGeneratorServiceImpl.MEDIA_ATTACHMENT_API_DESCRIPTOR_KEY;
import static com.enonic.xp.portal.impl.url.PortalUrlGeneratorServiceImpl.MEDIA_IMAGE_API_DESCRIPTOR_KEY;
import static com.enonic.xp.portal.impl.url.PortalUrlGeneratorServiceImpl.attachmentQueryParams;
import static com.enonic.xp.portal.impl.url.PortalUrlGeneratorServiceImpl.imageQueryParams;
import static com.enonic.xp.portal.impl.url.PortalUrlGeneratorServiceImpl.queryString;
import static java.util.Objects.requireNonNull;

/**
 * Resolves the parts of image and attachment URLs from configuration alone, for
 * {@link com.enonic.xp.portal.url.PortalUrlService}: the media is supplied, or named by id or path and looked up in the
 * project and branch of the params, or of the current context.
 */
final class MediaUrlParts
{
    private final ContentService contentService;

    MediaUrlParts( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    ImageUrlParts image( final ImageUrlPartsParams params )
    {
        final Supplier<ProjectName> projectName = projectName( params.getProjectName() );
        final Supplier<Branch> branch = branch( params.getBranch() );
        final Supplier<Media> media = Suppliers.memoize( params.getMedia() != null
                                                             ? params.getMedia()::get
                                                             : () -> MediaLookup.media( contentService, projectName.get(), branch.get(),
                                                                                        key( params.getId(), params.getPath() ) ) );

        final MediaPathParts parts = ImageMediaPathSupplier.create()
            .setMedia( media )
            .setProjectName( projectName )
            .setBranch( branch )
            .setScale( params.getScale() )
            .setFormat( params.getFormat() )
            .build()
            .parts();

        final String queryString = queryString(
            imageQueryParams( Map.of(), ImageScaling.isScalable( media.get() ), params.getQuality(), params.getBackground(),
                              params.getFilter() ) );

        return new ImageUrlParts( parts.path( MEDIA_IMAGE_API_DESCRIPTOR_KEY ), queryString,
                                  UrlBuilderHelper.urlEncodePathSegment( parts.context() ), parts.id(), parts.hash(),
                                  UrlBuilderHelper.urlEncodePathSegment( parts.scale() ), UrlBuilderHelper.urlEncodePathSegment( parts.name() ) );
    }

    AttachmentUrlParts attachment( final AttachmentUrlPartsParams params )
    {
        final Supplier<ProjectName> projectName = projectName( params.getProjectName() );
        final Supplier<Branch> branch = branch( params.getBranch() );
        final Supplier<Content> content = params.getContentSupplier() != null
            ? params.getContentSupplier()
            : () -> MediaLookup.content( contentService, projectName.get(), branch.get(), key( params.getId(), params.getPath() ) );

        final MediaPathParts parts = AttachmentMediaPathSupplier.create()
            .setContent( content )
            .setProjectName( projectName )
            .setBranch( branch )
            .setName( params.getName() )
            .setLabel( params.getLabel() )
            .build()
            .parts();

        return new AttachmentUrlParts( parts.path( MEDIA_ATTACHMENT_API_DESCRIPTOR_KEY ),
                                       queryString( attachmentQueryParams( Map.of(), params.isDownload() ) ),
                                       UrlBuilderHelper.urlEncodePathSegment( parts.context() ), parts.id(), parts.hash(),
                                       UrlBuilderHelper.urlEncodePathSegment( parts.name() ) );
    }

    /**
     * @return the project the params name, or else that of the current context
     */
    private static Supplier<ProjectName> projectName( final Supplier<ProjectName> projectName )
    {
        if ( projectName != null )
        {
            return projectName;
        }
        return Suppliers.memoize(
            () -> ProjectName.from( requireNonNull( ContextAccessor.current().getRepositoryId(), "Project must be provided" ) ) );
    }

    /**
     * @return the branch the params name, or else that of the current context
     */
    private static Supplier<Branch> branch( final Supplier<Branch> branch )
    {
        if ( branch != null )
        {
            return branch;
        }
        return Suppliers.memoize( () -> requireNonNull( ContextAccessor.current().getBranch(), "Branch must be provided" ) );
    }

    private static String key( final String id, final String path )
    {
        return id != null ? id : path;
    }
}
