package com.enonic.xp.portal.url;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Media;
import com.enonic.xp.project.ProjectName;

import static com.google.common.base.Strings.emptyToNull;
import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

/**
 * Parameters for generating an image URL from supplied media, project and branch values.
 * The selected style supplies quality, filter and background settings.
 * Scale and output format are independent of the style.
 */
@NullMarked
public final class ImageUrlGeneratorParams
{
    private final @Nullable String baseUrl;

    private final @Nullable String mediaBaseUrl;

    private final String urlType;

    private final Supplier<Media> mediaSupplier;

    private final Supplier<ProjectName> projectNameSupplier;

    private final Supplier<Branch> branchSupplier;

    private final @Nullable String background;

    private final @Nullable Integer quality;

    private final @Nullable String filter;

    private final @Nullable String format;

    private final String scale;

    private final @Nullable String style;

    private final Map<String, List<String>> queryParams;

    private ImageUrlGeneratorParams( final Builder builder )
    {
        this.baseUrl = builder.baseUrl;
        this.mediaBaseUrl = builder.mediaBaseUrl;
        this.urlType = requireNonNullElse( builder.urlType, UrlTypeConstants.SERVER_RELATIVE );
        this.mediaSupplier = requireNonNull( builder.mediaSupplier );
        this.projectNameSupplier = requireNonNull( builder.projectNameSupplier );
        this.branchSupplier = requireNonNull( builder.branchSupplier );
        this.style = emptyToNull( builder.style );
        com.enonic.xp.style.ImageStyleSettings.checkOverrides( style,
            builder.quality != null || builder.filter != null || builder.background != null );
        if ( style == null && ( "webp".equalsIgnoreCase( builder.format ) || "avif".equalsIgnoreCase( builder.format ) ) )
        {
            throw new IllegalArgumentException( "WebP and AVIF encoding requires a predefined image style" );
        }
        this.scale = requireNonNull( builder.scale );
        this.background = builder.background;
        this.quality = builder.quality;
        this.filter = builder.filter;
        this.format = builder.format;
        this.queryParams = builder.queryParams.build();
    }

    /**
     * Returns the explicit mount base URL.
     *
     * @return the mount base URL
     * @deprecated use {@link #getMediaBaseUrl()}. The replacement addresses the media API root;
     *     append {@code /_} when migrating a mount base URL.
     */
    @Deprecated( since = "8.2.0" )
    public @Nullable String getBaseUrl()
    {
        return baseUrl;
    }

    /**
     * Returns the explicit media API root.
     *
     * @return the media API root
     */
    public @Nullable String getMediaBaseUrl()
    {
        return mediaBaseUrl;
    }

    /**
     * Returns the requested URL representation.
     *
     * @return the URL type; defaults to {@link UrlTypeConstants#SERVER_RELATIVE}
     */
    public String getUrlType()
    {
        return urlType;
    }

    /**
     * Returns the supplier of the source media.
     *
     * @return the media supplier
     */
    public Supplier<Media> getMedia()
    {
        return mediaSupplier;
    }

    /**
     * Returns the supplier of the source project.
     *
     * @return the project supplier
     */
    public Supplier<ProjectName> getProjectName()
    {
        return projectNameSupplier;
    }

    /**
     * Returns the supplier of the source branch.
     *
     * @return the branch supplier
     */
    public Supplier<Branch> getBranch()
    {
        return branchSupplier;
    }

    /**
     * Returns the explicit background color override.
     *
     * @return the hexadecimal RGB color
     * @deprecated use {@link #getStyle()} to identify predefined processing settings.
     *     This accessor exposes only the legacy override, not the resolved style value.
     */
    @Deprecated( since = "8.2.0" )
    public @Nullable String getBackground()
    {
        return background;
    }

    /**
     * Returns the explicit encoder quality override.
     *
     * @return the encoder quality
     * @deprecated use {@link #getStyle()} to identify predefined processing settings.
     *     This accessor exposes only the legacy override, not the resolved style value.
     */
    @Deprecated( since = "8.2.0" )
    public @Nullable Integer getQuality()
    {
        return quality;
    }

    /**
     * Returns the explicit filter override.
     *
     * @return the filter specification
     * @deprecated use {@link #getStyle()} to identify predefined processing settings.
     *     This accessor exposes only the legacy override, not the resolved style value.
     */
    @Deprecated( since = "8.2.0" )
    public @Nullable String getFilter()
    {
        return filter;
    }

    /**
     * Returns the explicit output format, expressed as a filename extension.
     *
     * @return the requested output format
     */
    public @Nullable String getFormat()
    {
        return format;
    }

    /**
     * Returns the predefined image style selected for this request.
     *
     * @return the fully qualified {@code application:name} key
     */
    public @Nullable String getStyle()
    {
        return style;
    }

    /**
     * Returns the requested scaling operation.
     *
     * @return the scale expression
     */
    public String getScale()
    {
        return scale;
    }

    /**
     * Returns the additional query parameters.
     *
     * @return an unmodifiable map with unmodifiable value lists
     */
    public Map<String, List<String>> getQueryParams()
    {
        return queryParams;
    }

    /**
     * Creates a builder for image URL parameters.
     *
     * @return a new builder
     */
    public static Builder create()
    {
        return new Builder();
    }

    /**
     * Builds image URL parameters. Media, project and branch suppliers and a scale are required.
     * Values returned by the suppliers are resolved when a URL is generated.
     */
    public static class Builder
    {
        private @Nullable String baseUrl;

        private @Nullable String mediaBaseUrl;

        private @Nullable String urlType;

        private @Nullable Supplier<Media> mediaSupplier;

        private @Nullable Supplier<ProjectName> projectNameSupplier;

        private @Nullable Supplier<Branch> branchSupplier;

        private @Nullable String background;

        private @Nullable Integer quality;

        private @Nullable String filter;

        private @Nullable String format;

        private @Nullable String scale;

        private @Nullable String style;

        /**
         * Selects a predefined image style that supplies filter, quality and background settings.
         * Scale and format remain separate request parameters.
         *
         * @param style the fully qualified {@code application:name} key
         * @return this builder
         */
        public Builder setStyle( final @Nullable String style )
        {
            this.style = style;
            return this;
        }

        private final QueryParamsBuilder queryParams = new QueryParamsBuilder();

        /**
         * Sets a mount base URL, producing URLs in the form {@code <baseUrl>/_/media:image/...}.
         *
         * @param baseUrl the mount base URL
         * @return this builder
         * @deprecated use {@link #setMediaBaseUrl(String)}; append {@code /_} to retain the mount form
         */
        @Deprecated
        public Builder setBaseUrl( final @Nullable String baseUrl )
        {
            this.baseUrl = baseUrl;
            return this;
        }

        /**
         * Sets the API root used verbatim: {@code <mediaBaseUrl>/media:image/...}.
         *
         * @param mediaBaseUrl the media API root
         * @return this builder
         */
        public Builder setMediaBaseUrl( final @Nullable String mediaBaseUrl )
        {
            this.mediaBaseUrl = emptyToNull( mediaBaseUrl );
            return this;
        }

        /**
         * Selects the URL representation.
         *
         * @param urlType a URL type from {@link UrlTypeConstants}; the default is server-relative
         * @return this builder
         */
        public Builder setUrlType( final @Nullable String urlType )
        {
            this.urlType = urlType;
            return this;
        }

        /**
         * Supplies the source media when generating the URL.
         *
         * @param mediaSupplier a supplier returning the source media
         * @return this builder
         */
        public Builder setMedia( final @Nullable Supplier<Media> mediaSupplier )
        {
            this.mediaSupplier = mediaSupplier;
            return this;
        }

        /**
         * Supplies the project containing the source media.
         *
         * @param projectNameSupplier a supplier returning the project name
         * @return this builder
         */
        public Builder setProjectName( final @Nullable Supplier<ProjectName> projectNameSupplier )
        {
            this.projectNameSupplier = projectNameSupplier;
            return this;
        }

        /**
         * Supplies the branch containing the source media.
         *
         * @param branchSupplier a supplier returning the branch
         * @return this builder
         */
        public Builder setBranch( final @Nullable Supplier<Branch> branchSupplier )
        {
            this.branchSupplier = branchSupplier;
            return this;
        }

        /**
         * Sets the background for an unstyled image when flattening transparency.
         *
         * @param background one to six hexadecimal RGB digits, optionally prefixed by {@code 0x}; the default is white
         * @return this builder
         * @deprecated define background in an image style and select it with {@link #setStyle(String)}.
         *     Retained for unstyled requests; cannot be combined with a style.
         */
        @Deprecated( since = "8.2.0" )
        public Builder setBackground( final @Nullable String background )
        {
            this.background = background;
            return this;
        }

        /**
         * Sets the encoder quality for an unstyled image.
         *
         * @param quality a value from 0 through 100; the default is 85
         * @return this builder
         * @deprecated define quality in an image style and select it with {@link #setStyle(String)}.
         *     Retained for unstyled requests; cannot be combined with a style.
         */
        @Deprecated( since = "8.2.0" )
        public Builder setQuality( final @Nullable Integer quality )
        {
            this.quality = quality;
            return this;
        }

        /**
         * Sets the filter specification for an unstyled image.
         *
         * @param filter the filter specification
         * @return this builder
         * @deprecated define filter in an image style and select it with {@link #setStyle(String)}.
         *     Retained for unstyled requests; cannot be combined with a style.
         */
        @Deprecated( since = "8.2.0" )
        public Builder setFilter( final @Nullable String filter )
        {
            this.filter = filter;
            return this;
        }

        /**
         * Selects the output format independently of the style. WebP and AVIF require a style.
         * When no format is selected, the source format is retained.
         *
         * @param format an output extension such as {@code jpeg}, {@code png}, {@code gif}, {@code webp} or {@code avif}
         * @return this builder
         */
        public Builder setFormat( final @Nullable String format )
        {
            this.format = format;
            return this;
        }

        /**
         * Sets the required scaling operation independently of the style.
         * Examples include {@code width(640)} and {@code block(640,480)}.
         * A style reference must be supplied through {@link #setStyle(String)}.
         *
         * @param scale the scaling expression
         * @return this builder
         */
        public Builder setScale( final @Nullable String scale )
        {
            this.scale = scale;
            return this;
        }

        /**
         * Adds query parameters, replacing the values of any matching keys.
         * A styled URL rejects processing parameters in this map.
         *
         * @param queryParams query keys and their ordered value collections
         * @return this builder
         */
        public Builder setQueryParams( final Map<String, ? extends Collection<String>> queryParams )
        {
            this.queryParams.setQueryParams( queryParams );
            return this;
        }

        /**
         * Sets one query parameter, replacing any existing values for its key.
         * Styled URLs reject processing overrides supplied as query parameters.
         *
         * @param key the query parameter name
         * @param value the query parameter value
         * @return this builder
         */
        public Builder setQueryParam( final String key, final String value )
        {
            this.queryParams.setQueryParam( key, value );
            return this;
        }

        /**
         * Creates URL-generation parameters and validates required fields and style combinations.
         * Style existence, scale syntax and processing values are checked when the URL is generated.
         *
         * @return the URL-generation parameters
         * @throws NullPointerException if a required supplier or scale is missing, or a query key is missing
         * @throws IllegalArgumentException if a style has processing overrides, or WebP/AVIF is requested without a style
         */
        public ImageUrlGeneratorParams build()
        {
            return new ImageUrlGeneratorParams( this );
        }
    }
}
