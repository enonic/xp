package com.enonic.wem.portal.resource;

import com.enonic.wem.portal.AbstractRequestPath;

public class ResourceRequest
    extends AbstractRequestPath
{
    private String module;

    public String getModule()
    {
        return module;
    }

    public void setModule( final String module )
    {
        this.module = module;
    }

}
