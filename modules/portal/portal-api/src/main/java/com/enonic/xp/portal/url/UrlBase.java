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
 * Resolve it once and pass it to every {@link PageUrlPartsParams.Builder#setBase(UrlBase) page URL} and
 * {@link ProcessHtmlPartsParams.Builder#base(UrlBase) rich text} of the same request: it holds what they are resolved
 * for, so they look it up only once. It is a snapshot of the configuration at the time it was resolved.
 */
@NullMarked
public final class UrlBase
{
    private final ProjectName projectName;

    private final Branch branch;

    private final ContentPath path;

    private final @Nullable String baseUrl;

    private final ApplicationKeys applications;

    /**
     * @param projectName the project contents are looked up in
     * @param branch      the branch contents are looked up in
     * @param path        path of the site, or the root path for the project
     * @param baseUrl     the Base URL configured for the site or project, without a trailing slash; {@code null} when
     *                    none is configured
     * @param applications the applications configured on the site or project
     */
    public UrlBase( final ProjectName projectName, final Branch branch, final ContentPath path, final @Nullable String baseUrl,
                    final ApplicationKeys applications )
    {
        this.projectName = requireNonNull( projectName );
        this.branch = requireNonNull( branch );
        this.path = requireNonNull( path );
        this.baseUrl = baseUrl;
        this.applications = requireNonNull( applications );
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
     * @return path of the site, or the root path for the project: page paths are relative to it
     */
    public ContentPath getPath()
    {
        return path;
    }

    /**
     * @return the Base URL configured for the site or project, without a trailing slash; {@code null} when none is
     * configured
     */
    public @Nullable String getBaseUrl()
    {
        return baseUrl;
    }

    /**
     * @return the applications configured on the site or project, which image styles come from
     */
    public ApplicationKeys getApplications()
    {
        return applications;
    }

    @Override
    public String toString()
    {
        return "UrlBase{" + projectName + ":" + branch + path + ( baseUrl == null ? "" : ", " + baseUrl ) + "}";
    }
}
