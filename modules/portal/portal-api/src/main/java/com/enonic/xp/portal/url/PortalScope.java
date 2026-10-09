package com.enonic.xp.portal.url;

import java.util.function.Supplier;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.google.common.base.Suppliers;
import com.google.common.net.UrlEscapers;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

/**
 * An immutable, request-independent context for resolving page URLs and processing rich text below a selected content
 * or project.
 * <p>
 * It stands in for a site request: what request-following URLs take from the request - the project, the branch, the
 * site and its configuration - the URL parts take from the scope, without a request or its virtual host. Page paths are
 * relative to its content, as those of a request are relative to the content its virtual host mounts. It is resolved by
 * {@link PortalUrlService#portalScope(PortalScopeParams)}, as a snapshot of the configuration at that time.
 * <p>
 * Resolve it once for a selected content or project and pass it to every
 * {@link PageUrlPartsParams.Builder#setScope(PortalScope) page URL} and
 * {@link ProcessHtmlPartsParams.Builder#scope(PortalScope) rich text} resolved below it, so that they look it up only once.
 */
@NullMarked
public final class PortalScope
{
    private final ProjectName projectName;

    private final Branch branch;

    private final ContentPath path;

    private final ContentPath sitePath;

    private final SiteConfigs siteConfigs;

    private final Supplier<@Nullable String> baseUrl;

    private PortalScope( final Builder builder )
    {
        this.projectName = requireNonNull( builder.projectName, "projectName is required" );
        this.branch = requireNonNull( builder.branch, "branch is required" );
        this.path = requireNonNullElse( builder.path, ContentPath.ROOT );
        this.sitePath = requireNonNullElse( builder.sitePath, ContentPath.ROOT );
        this.siteConfigs = requireNonNullElse( builder.siteConfigs, SiteConfigs.empty() );
        this.baseUrl = Suppliers.memoize( this::resolveBaseUrl );
    }

    /**
     * @return the project contents are looked up in
     */
    public ProjectName getProjectName()
    {
        return projectName;
    }

    /**
     * @return the branch contents are looked up in
     */
    public Branch getBranch()
    {
        return branch;
    }

    /**
     * @return path of the content that page paths are relative to, or the root path for the project
     */
    public ContentPath getPath()
    {
        return path;
    }

    /**
     * @return path of the site at or above that content, or the root path for the project outside any site
     */
    public ContentPath getSitePath()
    {
        return sitePath;
    }

    /**
     * @return the configuration of the site or project
     */
    public SiteConfigs getSiteConfigs()
    {
        return siteConfigs;
    }

    /**
     * @return the Base URL configured for the site or project, followed by the path of the content below it, without a
     * trailing slash; {@code null} when none is configured
     */
    public @Nullable String getBaseUrl()
    {
        return baseUrl.get();
    }

    private @Nullable String resolveBaseUrl()
    {
        final SiteConfig portalConfig = siteConfigs.get( ApplicationKey.PORTAL );
        final String baseUrl = portalConfig != null ? portalConfig.getConfig().getString( "baseUrl" ) : null;
        if ( baseUrl == null || baseUrl.isEmpty() )
        {
            return null;
        }

        final StringBuilder url = new StringBuilder( baseUrl.endsWith( "/" ) ? baseUrl.substring( 0, baseUrl.length() - 1 ) : baseUrl );
        for ( int i = sitePath.elementCount(); i < path.elementCount(); i++ )
        {
            url.append( '/' ).append( UrlEscapers.urlPathSegmentEscaper().escape( path.getElement( i ).toString() ) );
        }
        return url.toString();
    }

    /**
     * @return a new builder
     */
    public static Builder create()
    {
        return new Builder();
    }

    /**
     * Builder of {@link PortalScope}. The project and the branch are required; by default the scope is the project
     * itself, without configuration.
     */
    public static final class Builder
    {
        private @Nullable ProjectName projectName;

        private @Nullable Branch branch;

        private @Nullable ContentPath path;

        private @Nullable ContentPath sitePath;

        private @Nullable SiteConfigs siteConfigs;

        private Builder()
        {
        }

        /**
         * @param projectName the project contents are looked up in
         * @return this builder
         */
        public Builder projectName( final ProjectName projectName )
        {
            this.projectName = projectName;
            return this;
        }

        /**
         * @param branch the branch contents are looked up in
         * @return this builder
         */
        public Builder branch( final Branch branch )
        {
            this.branch = branch;
            return this;
        }

        /**
         * @param path path of the content that page paths are relative to; the root path for the project
         * @return this builder
         */
        public Builder path( final ContentPath path )
        {
            this.path = path;
            return this;
        }

        /**
         * @param sitePath path of the site at or above that content; the root path for the project outside any site
         * @return this builder
         */
        public Builder sitePath( final ContentPath sitePath )
        {
            this.sitePath = sitePath;
            return this;
        }

        /**
         * @param siteConfigs the configuration of that site or project
         * @return this builder
         */
        public Builder siteConfigs( final SiteConfigs siteConfigs )
        {
            this.siteConfigs = siteConfigs;
            return this;
        }

        /**
         * @return the scope
         */
        public PortalScope build()
        {
            return new PortalScope( this );
        }
    }
}
