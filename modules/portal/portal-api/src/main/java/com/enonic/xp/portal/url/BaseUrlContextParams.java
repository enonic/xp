package com.enonic.xp.portal.url;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class BaseUrlContextParams
{
    private String projectName;

    private String branch;

    private String baseUrlKey;

    public String getProjectName()
    {
        return projectName;
    }

    public void setProjectName( final String projectName )
    {
        this.projectName = projectName;
    }

    public String getBranch()
    {
        return branch;
    }

    public void setBranch( final String branch )
    {
        this.branch = branch;
    }

    public String getBaseUrlKey()
    {
        return baseUrlKey;
    }

    public void setBaseUrlKey( final String baseUrlKey )
    {
        this.baseUrlKey = baseUrlKey;
    }
}
