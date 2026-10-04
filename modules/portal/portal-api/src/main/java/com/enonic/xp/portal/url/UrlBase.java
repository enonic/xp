package com.enonic.xp.portal.url;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.project.ProjectName;

import static java.util.Objects.requireNonNull;

/**
 * The site or project URLs belong to, resolved from configuration by {@link PortalUrlService#urlBase(UrlBaseParams)}.
 * <p>
 * It stands in for a site request: what request-following URLs take from the request - the project, the branch, the
 * site and its configuration - the URL parts take from the base, without a request or its virtual host. It is a
 * snapshot of the configuration at the time it was resolved.
 * <p>
 * Resolve it once and pass it to every {@link PageUrlPartsParams.Builder#setBase(UrlBase) page URL} and
 * {@link ProcessHtmlPartsParams.Builder#base(UrlBase) rich text} resolved for the same site, so that they look it up
 * only once.
 *
 * @param projectName  the project contents are looked up in
 * @param branch       the branch contents are looked up in
 * @param path         path of the site, or the root path for the project: page paths are relative to it
 * @param baseUrl      the Base URL configured for the site or project, without a trailing slash; {@code null} when none
 *                     is configured
 * @param applications the applications configured on the site or project
 */
@NullMarked
public record UrlBase(ProjectName projectName, Branch branch, ContentPath path, @Nullable String baseUrl, ApplicationKeys applications)
{
    public UrlBase
    {
        requireNonNull( projectName );
        requireNonNull( branch );
        requireNonNull( path );
        requireNonNull( applications );
    }
}
