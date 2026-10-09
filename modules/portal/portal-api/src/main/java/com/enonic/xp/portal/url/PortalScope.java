package com.enonic.xp.portal.url;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

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
 * Resolve it once and pass it to every {@link PageUrlPartsParams.Builder#setScope(PortalScope) page URL} and
 * {@link ProcessHtmlPartsParams.Builder#scope(PortalScope) rich text} resolved for the same site, so that they look it up
 * only once.
 *
 * @param projectName the project contents are looked up in
 * @param branch      the branch contents are looked up in
 * @param path        path of the content page paths are relative to, or the root path for the project
 * @param siteConfigs the configuration of the site at or above that content, or of the project outside any site
 * @param baseUrl     the Base URL configured there, followed by the path of the content below that site or project,
 *                    without a trailing slash; {@code null} when none is configured
 */
@NullMarked
public record PortalScope(ProjectName projectName, Branch branch, ContentPath path, SiteConfigs siteConfigs, @Nullable String baseUrl)
{
    public PortalScope
    {
        requireNonNull( projectName );
        requireNonNull( branch );
        requireNonNull( path );
        requireNonNull( siteConfigs );
    }

    /**
     * @return the applications configured on the site or project, in their order
     */
    public ApplicationKeys applications()
    {
        return ApplicationKeys.from( siteConfigs.stream().map( SiteConfig::getApplicationKey ).toList() );
    }
}
