package com.enonic.xp.portal.url;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.project.ProjectName;

import com.google.common.base.Strings;

/**
 * Parameters of {@link PortalUrlService#attachmentUrlParts(AttachmentUrlPartsParams)}: the content, which
 * of its attachments the URL addresses, and how it is served. The content is supplied, or named by id or path and
 * looked up in the project and branch. The caller supplies the base URL.
 */
@NullMarked
public final class AttachmentUrlPartsParams
{
    private final @Nullable Supplier<Content> contentSupplier;

    private final @Nullable String id;

    private final @Nullable String path;

    private final @Nullable Supplier<ProjectName> projectName;

    private final @Nullable Supplier<Branch> branch;

    private final boolean download;

    private final @Nullable String name;

    private final @Nullable String label;

    private final Map<String, List<String>> queryParams;

    private AttachmentUrlPartsParams( final Builder builder )
    {
        if ( ( builder.contentSupplier == null ) == ( builder.id == null && builder.path == null ) )
        {
            throw new IllegalArgumentException( "Either the content, or its id or path, is required" );
        }
        this.contentSupplier = builder.contentSupplier;
        this.id = builder.id;
        this.path = builder.path;
        this.projectName = builder.projectNameSupplier;
        this.branch = builder.branchSupplier;
        this.download = builder.download;
        this.name = builder.name;
        this.label = builder.label;
        this.queryParams = builder.queryParams.build();
    }

    /**
     * @return supplier of the content the attachment belongs to, or {@code null} when it is named by id or path
     */
    public @Nullable Supplier<Content> getContentSupplier()
    {
        return contentSupplier;
    }

    /**
     * @return id of the content to look up, or {@code null} when it is supplied or named by path
     */
    public @Nullable String getId()
    {
        return id;
    }

    /**
     * @return path of the content to look up, or {@code null} when it is supplied or named by id
     */
    public @Nullable String getPath()
    {
        return path;
    }

    /**
     * @return supplier of the project of the content, which forms the context segment of the path; {@code null} for
     * the project of the current context
     */
    public @Nullable Supplier<ProjectName> getProjectName()
    {
        return projectName;
    }

    /**
     * @return supplier of the branch of the content, which forms the context segment of the path; {@code null} for
     * the branch of the current context
     */
    public @Nullable Supplier<Branch> getBranch()
    {
        return branch;
    }

    /**
     * @return whether the URL asks for the attachment to be downloaded rather than shown inline
     */
    public boolean isDownload()
    {
        return download;
    }

    /**
     * @return name of the attachment, or {@code null} to pick it by label
     */
    public @Nullable String getName()
    {
        return name;
    }

    /**
     * @return label of the attachment, or {@code null} for {@code source}; used only when no name is set
     */
    public @Nullable String getLabel()
    {
        return label;
    }

    /**
     * @return additional query parameters, in the order they were set
     */
    public Map<String, List<String>> getQueryParams()
    {
        return queryParams;
    }

    /**
     * @return a new builder
     */
    public static Builder create()
    {
        return new Builder();
    }

    /**
     * Builder of {@link AttachmentUrlPartsParams}. Either the content or its id or path is required.
     */
    public static class Builder
    {
        private @Nullable Supplier<Content> contentSupplier;

        private @Nullable String id;

        private @Nullable String path;

        private @Nullable Supplier<ProjectName> projectNameSupplier;

        private @Nullable Supplier<Branch> branchSupplier;

        private boolean download;

        private @Nullable String name;

        private @Nullable String label;

        private final QueryParamsBuilder queryParams = new QueryParamsBuilder();

        /**
         * @param contentSupplier supplier of the content the attachment belongs to
         * @return this builder
         */
        public Builder setContent( final Supplier<Content> contentSupplier )
        {
            this.contentSupplier = contentSupplier;
            return this;
        }

        /**
         * Names the content by its id, to be looked up in the project and branch. Takes precedence over the path; the
         * alternative to {@link #setContent(Supplier)}.
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
         * Names the content by its path within the project, to be looked up in the project and branch; the alternative
         * to {@link #setContent(Supplier)}.
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
         * @param projectNameSupplier supplier of the project of the content; the project of the current context when
         *                            not set
         * @return this builder
         */
        public Builder setProjectName( final Supplier<ProjectName> projectNameSupplier )
        {
            this.projectNameSupplier = projectNameSupplier;
            return this;
        }

        /**
         * @param branchSupplier supplier of the branch of the content; the branch of the current context when not set
         * @return this builder
         */
        public Builder setBranch( final Supplier<Branch> branchSupplier )
        {
            this.branchSupplier = branchSupplier;
            return this;
        }

        /**
         * @param download {@code true} adds the {@code download} query parameter, which asks for the attachment to be
         *                 downloaded rather than shown inline
         * @return this builder
         */
        public Builder setDownload( final boolean download )
        {
            this.download = download;
            return this;
        }

        /**
         * @param name name of the attachment; {@code null} to pick it by label
         * @return this builder
         */
        public Builder setName( final @Nullable String name )
        {
            this.name = name;
            return this;
        }

        /**
         * @param label label of the attachment, used only when no name is set; {@code null} for {@code source}
         * @return this builder
         */
        public Builder setLabel( final @Nullable String label )
        {
            this.label = label;
            return this;
        }

        /**
         * @param key   name of an additional query parameter
         * @param value its value, replacing any earlier values
         * @return this builder
         */
        public Builder setQueryParam( final String key, final String value )
        {
            this.queryParams.setQueryParam( key, value );
            return this;
        }

        /**
         * @param queryParams additional query parameters; each replaces any earlier values of its name
         * @return this builder
         */
        public Builder setQueryParams( final Map<String, ? extends Collection<String>> queryParams )
        {
            this.queryParams.setQueryParams( queryParams );
            return this;
        }

        /**
         * @return the params
         * @throws IllegalArgumentException if neither or both of the content and its id or path are set
         */
        public AttachmentUrlPartsParams build()
        {
            return new AttachmentUrlPartsParams( this );
        }
    }
}
