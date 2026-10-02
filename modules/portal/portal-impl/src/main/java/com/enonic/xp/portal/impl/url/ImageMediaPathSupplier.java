package com.enonic.xp.portal.impl.url;

import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.Media;
import com.enonic.xp.portal.impl.ImageScaling;
import com.enonic.xp.portal.impl.MediaHashResolver;
import com.enonic.xp.project.ProjectName;

import static com.enonic.xp.portal.impl.url.UrlBuilderHelper.appendPart;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Objects.requireNonNull;

final class ImageMediaPathSupplier
    implements Supplier<String>
{
    private static final Logger LOG = LoggerFactory.getLogger( ImageMediaPathSupplier.class );

    static final String FULL_SCALE = "full";

    private final Supplier<Media> mediaSupplier;

    private final Supplier<ProjectName> projectNameSupplier;

    private final Supplier<Branch> branchSupplier;

    private final String scale;

    private final String format;

    private ImageMediaPathSupplier( final Builder builder )
    {
        this.scale = requireNonNull( builder.scale );
        this.mediaSupplier = builder.mediaSupplier;
        this.projectNameSupplier = builder.projectNameSupplier;
        this.branchSupplier = builder.branchSupplier;
        this.format = builder.format;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public String get()
    {
        final MediaPathParts parts = partsOrUnresolved();

        final StringBuilder url = new StringBuilder();

        appendPart( url, parts.context() );
        appendPart( url, parts.idWithHash() );
        appendPart( url, parts.scale() );
        appendPart( url, parts.name() );

        return url.toString();
    }

    /**
     * @return the segments of the URL, or of a URL the media API answers with 404 when the image does not resolve
     */
    MediaPathParts partsOrUnresolved()
    {
        try
        {
            return parts();
        }
        catch ( RuntimeException e )
        {
            final String id = resolvedId();
            LOG.warn( "Media [{}] does not resolve", id, e );
            return MediaPathParts.unresolved( id, resolveScale( scale ), null );
        }
    }

    private String resolvedId()
    {
        final String id = IdentifiedSupplier.contentId( mediaSupplier );
        if ( id != null )
        {
            return id;
        }
        try
        {
            return mediaSupplier.get().getId().toString();
        }
        catch ( RuntimeException e )
        {
            return null;
        }
    }

    MediaPathParts parts()
    {
        final ProjectName project = requireNonNull( projectNameSupplier.get() );
        final Branch branch = requireNonNull( branchSupplier.get() );
        final Media media = MediaLookup.image( requireNonNull( mediaSupplier.get() ) );

        final String context = project + ( ContentConstants.BRANCH_MASTER.equals( branch ) ? "" : ":" + branch );

        // an image served as stored has one URL, whatever the scale and format asked for
        final boolean scalable = ImageScaling.isScalable( media );

        return new MediaPathParts( context, media.getId().toString(), MediaHashResolver.resolveImageHash( media ),
                                   scalable ? resolveScale( scale ) : FULL_SCALE, resolveName( media, scalable ? format : null ) );
    }

    private String resolveName( final Content media, final String format )
    {
        final String name = media.getName().toString();

        if ( format != null )
        {
            final String extension = Files.getFileExtension( name );
            if ( isNullOrEmpty( extension ) || !format.equals( extension ) )
            {
                return name + "." + format;
            }
        }
        return name;
    }

    static String resolveScale( final String scale )
    {
        return scale.replaceAll( "\\s", "" ).replaceAll( "[(,]", "-" ).replace( ")", "" );
    }

    static class Builder
    {
        private Supplier<Media> mediaSupplier;

        private Supplier<ProjectName> projectNameSupplier;

        private Supplier<Branch> branchSupplier;

        private String scale;

        private String format;

        public Builder setMedia( final Supplier<Media> mediaSupplier )
        {
            this.mediaSupplier = mediaSupplier;
            return this;
        }

        public Builder setProjectName( final Supplier<ProjectName> projectNameSupplier )
        {
            this.projectNameSupplier = projectNameSupplier;
            return this;
        }

        public Builder setBranch( final Supplier<Branch> branchSupplier )
        {
            this.branchSupplier = branchSupplier;
            return this;
        }

        public Builder setScale( final String scale )
        {
            this.scale = scale;
            return this;
        }

        public Builder setFormat( final String format )
        {
            this.format = format;
            return this;
        }

        public ImageMediaPathSupplier build()
        {
            return new ImageMediaPathSupplier( this );
        }
    }
}
