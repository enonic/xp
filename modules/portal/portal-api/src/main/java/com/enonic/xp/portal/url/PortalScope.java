package com.enonic.xp.portal.url;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.google.common.net.UrlEscapers;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;

import static java.util.Objects.requireNonNull;

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

    private final @Nullable String baseUrl;

    private final ApplicationKeys applications;

    /**
     * @param projectName the project contents are looked up in
     * @param branch      the branch contents are looked up in
     * @param path        path of the content that page paths are relative to, or the root path for the project
     * @param sitePath    path of the site at or above that content, or the root path for the project outside any site
     * @param siteConfigs the configuration of that site or project
     */
    public PortalScope( final ProjectName projectName, final Branch branch, final ContentPath path, final ContentPath sitePath,
                        final SiteConfigs siteConfigs )
    {
        this.projectName = requireNonNull( projectName );
        this.branch = requireNonNull( branch );
        this.path = requireNonNull( path );
        this.sitePath = requireNonNull( sitePath );
        this.siteConfigs = requireNonNull( siteConfigs );
        this.baseUrl = baseUrl( siteConfigs, path, sitePath );
        this.applications = ApplicationKeys.from( siteConfigs.stream().map( SiteConfig::getApplicationKey ).toList() );
    }

    /**
     * @return the project contents are looked up in
     */
    public ProjectName projectName()
    {
        return projectName;
    }

    /**
     * @return the branch contents are looked up in
     */
    public Branch branch()
    {
        return branch;
    }

    /**
     * @return path of the content that page paths are relative to, or the root path for the project
     */
    public ContentPath path()
    {
        return path;
    }

    /**
     * @return path of the site at or above that content, or the root path for the project outside any site
     */
    public ContentPath sitePath()
    {
        return sitePath;
    }

    /**
     * @return the configuration of the site or project
     */
    public SiteConfigs siteConfigs()
    {
        return siteConfigs;
    }

    /**
     * @return the Base URL configured for the site or project, followed by the path of the content below it, without a
     * trailing slash; {@code null} when none is configured
     */
    public @Nullable String baseUrl()
    {
        return baseUrl;
    }

    /**
     * @return the applications configured on the site or project, in their order
     */
    public ApplicationKeys applications()
    {
        return applications;
    }

    private static @Nullable String baseUrl( final SiteConfigs siteConfigs, final ContentPath path, final ContentPath sitePath )
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
}
