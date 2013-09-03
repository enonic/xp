package com.enonic.wem.admin.rest.resource.content.model;


import java.util.Set;

public class DeleteContentParams
{
    private Set<String> contentPaths;

    public Set<String> getContentPaths()
    {
        return contentPaths;
    }

    public void setContentPaths( final Set<String> contentPaths )
    {
        this.contentPaths = contentPaths;
    }
}
