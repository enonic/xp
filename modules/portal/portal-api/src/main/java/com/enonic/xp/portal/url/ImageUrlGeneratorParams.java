package com.enonic.xp.portal.url;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Media;
import com.enonic.xp.project.ProjectName;

import static com.google.common.base.Strings.emptyToNull;
import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;


public final class ImageUrlGeneratorParams
{
    private final String baseUrl;

    private final String mediaBaseUrl;

    private final String urlType;

    private final Supplier<Media> mediaSupplier;

    private final Supplier<ProjectName> projectNameSupplier;

    private final Supplier<Branch> branchSupplier;

    private final String background;

    private final Integer quality;

    private final String filter;

    private final String format;

    private final String scale;

    private final Map<String, List<String>> queryParams;

    private ImageUrlGeneratorParams( final Builder builder )
    {
        this.baseUrl = builder.baseUrl;
        this.mediaBaseUrl = builder.mediaBaseUrl;
        this.urlType = requireNonNullElse( builder.urlType, UrlTypeConstants.SERVER_RELATIVE );
        this.mediaSupplier = requireNonNull( builder.mediaSupplier );
        this.projectNameSupplier = requireNonNull( builder.projectNameSupplier );
        this.branchSupplier = requireNonNull( builder.branchSupplier );
        this.scale = requireNonNull( builder.scale );
        this.background = builder.background;
        this.quality = builder.quality;
        this.filter = builder.filter;
        this.format = builder.format;
        this.queryParams = builder.queryParams.build();
    }

    public String getBaseUrl()
    {
        return baseUrl;
    }

    public String getMediaBaseUrl()
    {
        return mediaBaseUrl;
    }

    public String getUrlType()
    {
        return urlType;
    }

    public Supplier<Media> getMedia()
    {
        return mediaSupplier;
    }

    public Supplier<ProjectName> getProjectName()
    {
        return projectNameSupplier;
    }

    public Supplier<Branch> getBranch()
    {
        return branchSupplier;
    }

    public String getBackground()
    {
        return background;
    }

    public Integer getQuality()
    {
        return quality;
    }

    public String getFilter()
    {
        return filter;
    }

    public String getFormat()
    {
        return format;
    }

    public String getScale()
    {
        return scale;
    }

    public Map<String, List<String>> getQueryParams()
    {
        return queryParams;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private String baseUrl;

        private String mediaBaseUrl;

        private String urlType;

        private Supplier<Media> mediaSupplier;

        private Supplier<ProjectName> projectNameSupplier;

        private Supplier<Branch> branchSupplier;

        private String background;

        private Integer quality;

        private String filter;

        private String format;

        private String scale;

        private final QueryParamsBuilder queryParams = new QueryParamsBuilder();

        /**
         * Base URL of a mount where the generated media URL lives under the "_"
         * endpoint segment: {@code <baseUrl>/_/media:image/...}.
         *
         * @deprecated use {@link #setMediaBaseUrl(String)} - append {@code /_} to the
         * value to keep the mount form produced by this method.
         */
        @Deprecated
        public Builder setBaseUrl( final String baseUrl )
        {
            this.baseUrl = baseUrl;
            return this;
        }

        /**
         * Base URL used verbatim as the API root of the generated media URL:
         * {@code <mediaBaseUrl>/media:image/...} - no "_" endpoint segment is added.
         * Takes precedence over {@code baseUrl}, which points at a mount where APIs
         * live under the "_" endpoint segment: {@code <baseUrl>/_/media:image/...}.
         */
        public Builder setMediaBaseUrl( final String mediaBaseUrl )
        {
            this.mediaBaseUrl = emptyToNull( mediaBaseUrl );
            return this;
        }

        public Builder setUrlType( final String urlType )
        {
            this.urlType = urlType;
            return this;
        }

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

        public Builder setBackground( final String background )
        {
            this.background = background;
            return this;
        }

        public Builder setQuality( final Integer quality )
        {
            this.quality = quality;
            return this;
        }

        public Builder setFilter( final String filter )
        {
            this.filter = filter;
            return this;
        }

        public Builder setFormat( final String format )
        {
            this.format = format;
            return this;
        }

        public Builder setScale( final String scale )
        {
            this.scale = scale;
            return this;
        }

        public Builder setQueryParams( final Map<String, ? extends Collection<String>> queryParams )
        {
            this.queryParams.setQueryParams( queryParams );
            return this;
        }

        public Builder setQueryParam( final String key, final String value )
        {
            this.queryParams.setQueryParam( key, value );
            return this;
        }

        public ImageUrlGeneratorParams build()
        {
            return new ImageUrlGeneratorParams( this );
        }
    }
}
