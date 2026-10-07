package com.enonic.xp.portal.url;

import java.util.function.Supplier;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Media;
import com.enonic.xp.project.ProjectName;

import com.google.common.base.Strings;

import static java.util.Objects.requireNonNullElse;

/**
 * Parameters of {@link PortalUrlService#imageUrlParts(ImageUrlPartsParams)}: the image, and how the image
 * API is asked to process it. The image is supplied, or named by id or path and looked up in the project and branch.
 * The caller supplies the base URL.
 */
@NullMarked
public final class ImageUrlPartsParams
{
    private final @Nullable Supplier<Media> mediaSupplier;

    private final @Nullable String id;

    private final @Nullable String path;

    private final @Nullable Supplier<ProjectName> projectNameSupplier;

    private final @Nullable Supplier<Branch> branchSupplier;

    private final @Nullable String background;

    private final @Nullable Integer quality;

    private final @Nullable String filter;

    private final @Nullable String format;

    private static final String FULL_SCALE = "full";

    private final String scale;

    private ImageUrlPartsParams( final Builder builder )
    {
        if ( ( builder.mediaSupplier == null ) == ( builder.id == null && builder.path == null ) )
        {
            throw new IllegalArgumentException( "Either media or id/path must be provided, but not both" );
        }
        this.mediaSupplier = builder.mediaSupplier;
        this.id = builder.id;
        this.path = builder.path;
        this.projectNameSupplier = builder.projectNameSupplier;
        this.branchSupplier = builder.branchSupplier;
        this.scale = requireNonNullElse( builder.scale, FULL_SCALE );
        this.background = builder.background;
        this.quality = builder.quality;
        this.filter = builder.filter;
        this.format = builder.format;
    }

    /**
     * @return supplier of the image, or {@code null} when it is named by id or path
     */
    public @Nullable Supplier<Media> getMedia()
    {
        return mediaSupplier;
    }

    /**
     * @return id of the image to look up, or {@code null} when it is supplied or named by path
     */
    public @Nullable String getId()
    {
        return id;
    }

    /**
     * @return path of the image to look up, or {@code null} when it is supplied or named by id
     */
    public @Nullable String getPath()
    {
        return path;
    }

    /**
     * @return supplier of the project of the image, which forms the context segment of the path; {@code null} for the
     * project of the current context
     */
    public @Nullable Supplier<ProjectName> getProjectName()
    {
        return projectNameSupplier;
    }

    /**
     * @return supplier of the branch of the image, which forms the context segment of the path; {@code null} for the
     * branch of the current context
     */
    public @Nullable Supplier<Branch> getBranch()
    {
        return branchSupplier;
    }

    /**
     * @return background color, or {@code null} for none
     */
    public @Nullable String getBackground()
    {
        return background;
    }

    /**
     * @return compression quality, or {@code null} for the default
     */
    public @Nullable Integer getQuality()
    {
        return quality;
    }

    /**
     * @return image filters, or {@code null} for none
     */
    public @Nullable String getFilter()
    {
        return filter;
    }

    /**
     * @return image format, or {@code null} to keep that of the image
     */
    public @Nullable String getFormat()
    {
        return format;
    }

    /**
     * @return the scaling function; {@code full} for the image as stored when none is set
     */
    public String getScale()
    {
        return scale;
    }

    /**
     * @return a new builder
     */
    public static Builder create()
    {
        return new Builder();
    }

    /**
     * Builder of {@link ImageUrlPartsParams}. Either the media or its id/path is required, but not both; without a scale, the image is as stored.
     */
    public static class Builder
    {
        private @Nullable Supplier<Media> mediaSupplier;

        private @Nullable String id;

        private @Nullable String path;

        private @Nullable Supplier<ProjectName> projectNameSupplier;

        private @Nullable Supplier<Branch> branchSupplier;

        private @Nullable String background;

        private @Nullable Integer quality;

        private @Nullable String filter;

        private @Nullable String format;

        private @Nullable String scale;

        /**
         * @param mediaSupplier supplier of the image
         * @return this builder
         */
        public Builder setMedia( final Supplier<Media> mediaSupplier )
        {
            this.mediaSupplier = mediaSupplier;
            return this;
        }

        /**
         * Names the image by its id, to be looked up in the project and branch. Takes precedence over the path; the
         * alternative to {@link #setMedia(Supplier)}.
         *
         * @param id content id; {@code null} or empty clears it
         * @return this builder
         */
        public Builder setId( final @Nullable String id )
        {
            this.id = Strings.emptyToNull( id );
            return this;
        }

        /**
         * Names the image by its path within the project, to be looked up in the project and branch; the alternative
         * to {@link #setMedia(Supplier)}.
         *
         * @param path content path; {@code null} or empty clears it
         * @return this builder
         */
        public Builder setPath( final @Nullable String path )
        {
            this.path = Strings.emptyToNull( path );
            return this;
        }

        /**
         * @param projectNameSupplier supplier of the project of the image; the project of the current context when not
         *                            set
         * @return this builder
         */
        public Builder setProjectName( final Supplier<ProjectName> projectNameSupplier )
        {
            this.projectNameSupplier = projectNameSupplier;
            return this;
        }

        /**
         * @param branchSupplier supplier of the branch of the image; the branch of the current context when not set
         * @return this builder
         */
        public Builder setBranch( final Supplier<Branch> branchSupplier )
        {
            this.branchSupplier = branchSupplier;
            return this;
        }

        /**
         * @param background background color for images with transparency, as a hex value such as {@code ff0000};
         *                   {@code null} for none
         * @return this builder
         */
        public Builder setBackground( final @Nullable String background )
        {
            this.background = background;
            return this;
        }

        /**
         * @param quality JPEG compression quality, from 0 to 100; {@code null} for the default
         * @return this builder
         */
        public Builder setQuality( final @Nullable Integer quality )
        {
            this.quality = quality;
            return this;
        }

        /**
         * @param filter image filters, such as {@code rounded(5);sharpen()}; {@code null} for none
         * @return this builder
         */
        public Builder setFilter( final @Nullable String filter )
        {
            this.filter = filter;
            return this;
        }

        /**
         * @param format image format, applied as the extension of the name segment; {@code null} to keep that of the
         *               image
         * @return this builder
         */
        public Builder setFormat( final @Nullable String format )
        {
            this.format = format;
            return this;
        }

        /**
         * @param scale scaling function, such as {@code block(800,200)} or {@code width(768)}; {@code null} for {@code full},
         *              the image as stored
         * @return this builder
         */
        public Builder setScale( final @Nullable String scale )
        {
            this.scale = scale;
            return this;
        }

        /**
         * @return the params
         * @throws IllegalArgumentException if neither or both of the media and its id or path are set
         */
        public ImageUrlPartsParams build()
        {
            return new ImageUrlPartsParams( this );
        }
    }
}
