package com.enonic.xp.portal.url;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.project.ProjectName;

import static com.google.common.base.Strings.emptyToNull;
import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;


public final class AttachmentUrlGeneratorParams
{
    private final String baseUrl;

    private final String mediaBaseUrl;

    private final String urlType;

    private final Supplier<Content> contentSupplier;

    private final Supplier<ProjectName> projectName;

    private final Supplier<Branch> branch;

    private final boolean download;

    private final String name;

    private final String label;

    private final Map<String, List<String>> queryParams;

    private AttachmentUrlGeneratorParams( final Builder builder )
    {
        this.baseUrl = builder.baseUrl;
        this.mediaBaseUrl = builder.mediaBaseUrl;
        this.urlType = requireNonNullElse( builder.urlType, UrlTypeConstants.SERVER_RELATIVE );
        this.contentSupplier = requireNonNull( builder.contentSupplier );
        this.projectName = requireNonNull( builder.projectNameSupplier );
        this.branch = requireNonNull( builder.branchSupplier );
        this.download = builder.download;
        this.name = builder.name;
        this.label = builder.label;
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

    public Supplier<Content> getContentSupplier()
    {
        return contentSupplier;
    }

    public Supplier<ProjectName> getProjectName()
    {
        return projectName;
    }

    public Supplier<Branch> getBranch()
    {
        return branch;
    }

    public boolean isDownload()
    {
        return download;
    }

    public String getName()
    {
        return name;
    }

    public String getLabel()
    {
        return label;
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

        private Supplier<Content> contentSupplier;

        private Supplier<ProjectName> projectNameSupplier;

        private Supplier<Branch> branchSupplier;

        private boolean download;

        private String name;

        private String label;

        private final QueryParamsBuilder queryParams = new QueryParamsBuilder();

        /**
         * Base URL of a mount where the generated media URL lives under the "_"
         * endpoint segment: {@code <baseUrl>/_/media:attachment/...}.
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
         * {@code <mediaBaseUrl>/media:attachment/...} - no "_" endpoint segment is added.
         * Takes precedence over {@code baseUrl}, which points at a mount where APIs
         * live under the "_" endpoint segment: {@code <baseUrl>/_/media:attachment/...}.
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

        public Builder setContent( final Supplier<Content> contentSupplier )
        {
            this.contentSupplier = contentSupplier;
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

        public Builder setDownload( final boolean download )
        {
            this.download = download;
            return this;
        }

        public Builder setName( final String name )
        {
            this.name = name;
            return this;
        }

        public Builder setLabel( final String label )
        {
            this.label = label;
            return this;
        }

        public Builder setQueryParam( final String key, final String value )
        {
            this.queryParams.setQueryParam( key, value );
            return this;
        }

        public Builder setQueryParams( final Map<String, ? extends Collection<String>> queryParams )
        {
            this.queryParams.setQueryParams( queryParams );
            return this;
        }

        public AttachmentUrlGeneratorParams build()
        {
            return new AttachmentUrlGeneratorParams( this );
        }
    }
}
