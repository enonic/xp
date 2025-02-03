package com.enonic.xp.portal.impl.url3;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.url.BaseUrlStrategy;
import com.enonic.xp.project.ProjectName;

public class OfflineBaseUrlStrategy
    implements BaseUrlStrategy
{
    private final ContentService contentService;

    private final ProjectName projectName;

    private final Branch branch;

    private final String siteKey;

    public OfflineBaseUrlStrategy( final ContentService contentService, final ProjectName projectName, final Branch branch,
                                   final String siteKey )
    {
        this.contentService = contentService;
        this.projectName = projectName;
        this.branch = branch;
        this.siteKey = siteKey;
    }

    @Override
    public String generateBaseUrl()
    {
        return "/";
    }
}
