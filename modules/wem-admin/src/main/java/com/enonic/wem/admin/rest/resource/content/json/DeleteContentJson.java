package com.enonic.wem.admin.rest.resource.content.json;


import java.util.Set;

public class DeleteContentJson
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
