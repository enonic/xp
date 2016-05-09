package com.enonic.xp.admin.impl.rest.resource.content.json;


import java.util.Set;

public class DeleteContentJson
{
    private Set<String> contentPaths;

    private boolean deleteOnline;

    public Set<String> getContentPaths()
    {
        return contentPaths;
    }

    public void setContentPaths( final Set<String> contentPaths )
    {
        this.contentPaths = contentPaths;
    }

    public boolean isDeleteOnline()
    {
        return deleteOnline;
    }

    public void setDeleteOnline( boolean deleteOnline )
    {
        this.deleteOnline = deleteOnline;
    }

}
