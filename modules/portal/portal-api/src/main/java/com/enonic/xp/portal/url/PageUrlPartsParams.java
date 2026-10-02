package com.enonic.xp.portal.url;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.google.common.base.Strings;

import static java.util.Objects.requireNonNull;

/**
 * Parameters of {@link PortalUrlService#pageUrlParts(PageUrlPartsParams)}: the content a page URL addresses, by
 * {@link Builder#setId(String) id} or {@link Builder#setPath(String) path}, and the site or project the URL belongs to.
 */
@NullMarked
public final class PageUrlPartsParams
{
    private final @Nullable String id;

    private final @Nullable String path;

    private final BaseUrlParams base;

    private final Map<String, List<String>> queryParams;

    private PageUrlPartsParams( final Builder builder )
    {
        if ( builder.id == null && builder.path == null )
        {
            throw new IllegalArgumentException( "Either id or path is required" );
        }
        this.id = builder.id;
        this.path = builder.path;
        this.base = requireNonNull( builder.base, "base" );
        this.queryParams = builder.queryParams.build();
    }

    /**
     * @return id of the content the URL addresses, or {@code null} when it is named by path
     */
    public @Nullable String getId()
    {
        return id;
    }

    /**
     * @return path of the content the URL addresses, or {@code null} when it is named by id
     */
    public @Nullable String getPath()
    {
        return path;
    }

    /**
     * @return the site or project the URL belongs to
     * @see Builder#setBase(BaseUrlParams)
     */
    public BaseUrlParams getBase()
    {
        return base;
    }

    /**
     * @return query parameters of the URL, in the order they were set
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
     * Builder of {@link PageUrlPartsParams}. The base, and either the id or the path, are required.
     */
    public static final class Builder
    {
        private @Nullable String id;

        private @Nullable String path;

        private @Nullable BaseUrlParams base;

        private final QueryParamsBuilder queryParams = new QueryParamsBuilder();

        private Builder()
        {
        }

        /**
         * Names the content the URL addresses by its id; takes precedence over the path.
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
         * Names the content the URL addresses by its path within the project.
         *
         * @param path content path, such as {@code /my-site/posts/first-post}; {@code null} or empty clears it
         * @return this builder
         */
        public Builder setPath( final @Nullable String path )
        {
            this.path = Strings.emptyToNull( path );
            return this;
        }

        /**
         * Selects the site - or the project - the URL belongs to, by the same parameters
         * {@link PortalUrlService#baseUrl(BaseUrlParams)} takes: the nearest one at or above the content they name.
         * A project contains sites and a site can contain further sites, so this picks a level of that containment:
         * {@code "/"} names the project, a site path or id names that site. The project and branch it names are the
         * ones the content is looked up in.
         * <p>
         * Each level carries its own configuration, so the Base URL configured on the selected level applies, and the
         * path of the URL is relative to it. The content has to be inside the selected level, or be that level itself;
         * for a content elsewhere {@link ContentOutOfScopeException} is thrown.
         *
         * @param base the site or project the URL belongs to
         * @return this builder
         */
        public Builder setBase( final BaseUrlParams base )
        {
            this.base = base;
            return this;
        }

        /**
         * @param key   name of a query parameter
         * @param value its value, replacing any earlier values
         * @return this builder
         */
        public Builder setQueryParam( final String key, final String value )
        {
            this.queryParams.setQueryParam( key, value );
            return this;
        }

        /**
         * @param queryParams query parameters; each replaces any earlier values of its name
         * @return this builder
         */
        public Builder setQueryParams( final Map<String, ? extends Collection<String>> queryParams )
        {
            this.queryParams.setQueryParams( queryParams );
            return this;
        }

        /**
         * @return the params
         * @throws NullPointerException     if the base is not set
         * @throws IllegalArgumentException if neither the id nor the path is set
         */
        public PageUrlPartsParams build()
        {
            return new PageUrlPartsParams( this );
        }
    }
}
