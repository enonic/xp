package com.enonic.xp.portal.impl.url;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.site.SiteConfigs;

/**
 * What a base URL is resolved from.
 *
 * @param projectName the project contents are looked up in
 * @param branch      the branch contents are looked up in
 * @param baseUrl     the base URL, or {@code null} when none is configured
 * @param content     the content the base names, or {@code null} for the project root or a site request
 * @param anchorPath  the path of the level the base URL belongs to: the nearest site, or the project root
 * @param siteConfigs the configuration of that level
 */
record BaseUrlMetadata(ProjectName projectName, Branch branch, String baseUrl, Content content, ContentPath anchorPath,
                       SiteConfigs siteConfigs)
{
}
