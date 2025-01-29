package com.enonic.xp.portal.impl.url3;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.project.ProjectName;

public class ContextPathPrefixStrategy
    implements PathPrefixStrategy
{
    private final ProjectName projectName;

    private final Branch branch;

    private final String siteKey;

    public ContextPathPrefixStrategy( final ProjectName projectName, final Branch branch, final String siteKey )
    {
        this.projectName = projectName;
        this.branch = branch;
        this.siteKey = siteKey;
    }

    @Override
    public String generatePathPrefix()
    {
        return "/site/" + projectName + "/" + branch + "/" + siteKey + "/_";
    }
}
