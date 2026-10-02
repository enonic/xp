package com.enonic.xp.portal.url;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.project.ProjectName;

/**
 * Parameters of {@link PortalUrlService#urlBase(UrlBaseParams)}: the site or project URLs belong to, named by a content
 * at or below it, and the project and branch it is in.
 */
@NullMarked
public final class UrlBaseParams
{
    private final @Nullable ContentId contentId;

    private final @Nullable ContentPath contentPath;

    private final @Nullable ProjectName projectName;

    private final @Nullable Branch branch;

    private UrlBaseParams( final Builder builder )
    {
        this.contentId = builder.contentId;
        this.contentPath = builder.contentPath;
        this.projectName = builder.projectName;
        this.branch = builder.branch;
    }

    /**
     * @return id of the content naming the site or project, or {@code null} when it is named by path or is the project
     */
    public @Nullable ContentId getContentId()
    {
        return contentId;
    }

    /**
     * @return path of the content naming the site or project, or {@code null} when it is named by id or is the project
     */
    public @Nullable ContentPath getContentPath()
    {
        return contentPath;
    }

    /**
     * @return the project, or {@code null} to take it from the context
     */
    public @Nullable ProjectName getProjectName()
    {
        return projectName;
    }

    /**
     * @return the branch, or {@code null} to take it from the context
     */
    public @Nullable Branch getBranch()
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
     * <p>
     * The site or project is the nearest one at or above the content named by {@link #setContentId(ContentId) id} or
     * {@link #setContentPath(ContentPath) path}, the id taking precedence. A project contains sites and a site can
     * contain further sites, so this picks a level of that containment: the root path names the project, a site, or a
     * content inside it, names that site.
     */
    public static final class Builder
    {
        private @Nullable ContentId contentId;

        private @Nullable ContentPath contentPath;

        private @Nullable ProjectName projectName;

        private @Nullable Branch branch;

        private Builder()
        {
        }

        /**
         * Names the site, or a content inside it, by id; takes precedence over the path.
         *
         * @param contentId content id; {@code null} clears it
         * @return this builder
         */
        public Builder setContentId( final @Nullable ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        /**
         * Names the site, or a content inside it, by path; the root path names the project.
         *
         * @param contentPath content path; {@code null} clears it
         * @return this builder
         */
        public Builder setContentPath( final @Nullable ContentPath contentPath )
        {
            this.contentPath = contentPath;
            return this;
        }

        /**
         * Sets the project the base, and the contents addressed from it, are looked up in.
         *
         * @param projectName the project; {@code null} takes it from the context
         * @return this builder
         */
        public Builder setProjectName( final @Nullable ProjectName projectName )
        {
            this.projectName = projectName;
            return this;
        }

        /**
         * Sets the branch the base, and the contents addressed from it, are looked up in.
         *
         * @param branch the branch; {@code null} takes it from the context
         * @return this builder
         */
        public Builder setBranch( final @Nullable Branch branch )
        {
            this.branch = branch;
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
