package com.enonic.xp.portal.url;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class BaseUrlParams
{
    private String projectName;

    private String branch;

    private String key;

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

    public String getKey()
    {
        return key;
    }

    public void setKey( final String key )
    {
        this.key = key;
    }
}
