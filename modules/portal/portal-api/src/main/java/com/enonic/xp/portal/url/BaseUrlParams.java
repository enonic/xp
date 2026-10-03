package com.enonic.xp.portal.url;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.google.common.base.MoreObjects;

import static java.util.Objects.requireNonNullElse;

/**
 * Parameters of {@link PortalUrlService#baseUrl(BaseUrlParams)}: the {@link Builder#setUrlType(String) URL type} of
 * the base URL of the current site request.
 * <p>
 * The content, project and branch setters are deprecated: they resolve the base URL from configuration, which
 * {@link PortalUrlService#urlBase(UrlBaseParams)} does.
 *
 * @see PortalUrlService#baseUrl(BaseUrlParams)
 */
@NullMarked
public final class BaseUrlParams
{
    private final String urlType;

    private final @Nullable String projectName;

    private final @Nullable String branch;

    private final @Nullable String id;

    private final @Nullable String path;

    private BaseUrlParams( final Builder builder )
    {
        this.urlType = requireNonNullElse( builder.urlType, UrlTypeConstants.SERVER_RELATIVE );
        this.projectName = builder.projectName;
        this.branch = builder.branch;
        this.id = builder.id;
        this.path = builder.path;
    }

    /**
     * @return one of {@link UrlTypeConstants}, {@link UrlTypeConstants#SERVER_RELATIVE} unless set. It applies only
     * to a base URL that follows the current request; a configured Base URL is used as it is
     */
    public String getUrlType()
    {
        return urlType;
    }

    /**
     * @return project of the content, or {@code null} to take it from the context
     */
    public @Nullable String getProjectName()
    {
        return projectName;
    }

    /**
     * @return branch of the content, or {@code null} to take it from the context
     */
    public @Nullable String getBranch()
    {
        return branch;
    }

    /**
     * @return id of the content, or {@code null} when it is named by path
     */
    public @Nullable String getId()
    {
        return id;
    }

    /**
     * @return path of the content within the project, or {@code null} when it is named by id
     */
    public @Nullable String getPath()
    {
        return path;
    }

    /**
     * @return a new builder
     */

    public static Builder create()
    {
        return new Builder();
    }

    /**
     * Builder of {@link BaseUrlParams}.
     */
    public static class Builder
    {
        private @Nullable String urlType;

        private @Nullable String projectName;

        private @Nullable String branch;

        private @Nullable String id;

        private @Nullable String path;

        /**
         * @param urlType one of {@link UrlTypeConstants}, for a base URL that follows the current request;
         *                {@code null} for {@link UrlTypeConstants#SERVER_RELATIVE}
         * @return this builder
         */
        public Builder setUrlType( final @Nullable String urlType )
        {
            this.urlType = urlType;
            return this;
        }

        /**
         * Sets the project of the content. The base URL is then resolved from configuration in place of the request.
         *
         * @param projectName project of the content; {@code null} to take it from the context
         * @return this builder
         * @deprecated use {@link PortalUrlService#urlBase(UrlBaseParams)}, with the project set on its params
         */
        @Deprecated
        public Builder setProjectName( final @Nullable String projectName )
        {
            this.projectName = projectName;
            return this;
        }

        /**
         * Sets the branch of the content. The base URL is then resolved from configuration in place of the request.
         *
         * @param branch branch of the content; {@code null} to take it from the context
         * @return this builder
         * @deprecated use {@link PortalUrlService#urlBase(UrlBaseParams)}, with the branch set on its params
         */
        @Deprecated
        public Builder setBranch( final @Nullable String branch )
        {
            this.branch = branch;
            return this;
        }

        /**
         * Names the content by its id, whose nearest site the base URL belongs to off a site request.
         *
         * @param id content id
         * @return this builder
         * @deprecated use {@link PortalUrlService#urlBase(UrlBaseParams)}, with the content set on its params
         */
        @Deprecated
        public Builder setId( final @Nullable String id )
        {
            this.id = id;
            return this;
        }

        /**
         * Names the content by its path within the project, whose nearest site the base URL belongs to off a site
         * request; {@code "/"} names the project itself.
         *
         * @param path content path
         * @return this builder
         * @deprecated use {@link PortalUrlService#urlBase(UrlBaseParams)}, with the content set on its params
         */
        @Deprecated
        public Builder setPath( final @Nullable String path )
        {
            this.path = path;
            return this;
        }

        /**
         * @return the params
         */
        public BaseUrlParams build()
        {
            return new BaseUrlParams( this );
        }
    }

    @Override
    public String toString()
    {
        final MoreObjects.ToStringHelper helper = MoreObjects.toStringHelper( this );
        helper.omitNullValues();
        helper.add( "type", this.urlType );
        helper.add( "id", this.id );
        helper.add( "path", this.path );
        helper.add( "project", this.projectName );
        helper.add( "branch", this.branch );
        return helper.toString();
    }
}
