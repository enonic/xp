package com.enonic.xp.portal.url;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.google.common.base.Strings;

/**
 * Parameters of {@link PortalUrlService#urlBase(UrlBaseParams)}: the site or project URLs belong to, and the project
 * and branch it is in.
 */
@NullMarked
public final class UrlBaseParams
{
    private final String key;

    private final @Nullable String projectName;

    private final @Nullable String branch;

    private UrlBaseParams( final Builder builder )
    {
        this.key = builder.key;
        this.projectName = builder.projectName;
        this.branch = builder.branch;
    }

    /**
     * @return key of a content naming the site or project: an id, or a path; {@code "/"} is the project
     * @see Builder#setKey(String)
     */
    public String getKey()
    {
        return key;
    }

    /**
     * @return the project, or {@code null} to take it from the context
     */
    public @Nullable String getProjectName()
    {
        return projectName;
    }

    /**
     * @return the branch, or {@code null} to take it from the context
     */
    public @Nullable String getBranch()
    {
        return branch;
    }

    /**
     * @return a new builder
     */
    public static Builder create()
    {
        return new Builder();
    }

    /**
     * Builder of {@link UrlBaseParams}. Every parameter is optional: by default the base is the project of the current
     * context.
     */
    public static final class Builder
    {
        private String key = "/";

        private @Nullable String projectName;

        private @Nullable String branch;

        private Builder()
        {
        }

        /**
         * Selects the site - or the project - URLs belong to: the nearest one at or above the content the key names.
         * A project contains sites and a site can contain further sites, so this picks a level of that containment:
         * {@code "/"} names the project, a site path or id names that site. Defaults to the project.
         *
         * @param key key of a content: an id, or a path, starting with {@code "/"}; {@code null} or empty selects the
         *            project
         * @return this builder
         */
        public Builder setKey( final @Nullable String key )
        {
            this.key = Strings.isNullOrEmpty( key ) ? "/" : key;
            return this;
        }

        /**
         * Sets the project the base, and the contents addressed from it, are looked up in.
         *
         * @param projectName project name; {@code null} or empty takes it from the context
         * @return this builder
         */
        public Builder setProjectName( final @Nullable String projectName )
        {
            this.projectName = Strings.emptyToNull( projectName );
            return this;
        }

        /**
         * Sets the branch the base, and the contents addressed from it, are looked up in.
         *
         * @param branch branch name; {@code null} or empty takes it from the context
         * @return this builder
         */
        public Builder setBranch( final @Nullable String branch )
        {
            this.branch = Strings.emptyToNull( branch );
            return this;
        }

        /**
         * @return the params
         */
        public UrlBaseParams build()
        {
            return new UrlBaseParams( this );
        }
    }
}
