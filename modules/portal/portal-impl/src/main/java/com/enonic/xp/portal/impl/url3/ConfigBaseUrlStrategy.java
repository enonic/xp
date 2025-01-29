package com.enonic.xp.portal.impl.url3;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.project.ProjectName;

public class ConfigBaseUrlStrategy
    implements BaseUrlStrategy
{
    private final ProjectName projectName;

    private final Branch branch;

    private final String siteKey;

    public ConfigBaseUrlStrategy( final ProjectName projectName, final Branch branch, final String siteKey )
    {
        this.projectName = projectName;
        this.branch = branch;
        this.siteKey = siteKey;
    }

    @Override
    public String generateBaseUrl()
    {
        return "https://cdn.enonic.com";
    }
}
