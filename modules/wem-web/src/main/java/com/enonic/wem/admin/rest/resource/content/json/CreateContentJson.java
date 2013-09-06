package com.enonic.wem.admin.rest.resource.content.json;


import com.enonic.wem.api.command.content.CreateContentResult;

public class CreateContentJson
{
    private boolean created;

    private String contentId;

    private String contentPath;

    private String failure;

    public CreateContentJson( final CreateContentResult result )
    {
        if ( result != null )
        {
            contentId = result.getContentId().toString();
            contentPath = result.getContentPath().toString();
        }
        created = result != null;
    }

    public CreateContentJson( final Throwable e )
    {
        failure = e.getMessage();
        created = false;
    }

    public boolean isCreated()
    {
        return created;
    }

    public String getContentId()
    {
        return contentId;
    }

    public String getContentPath()
    {
        return contentPath;
    }

    public String getFailure()
    {
        return failure;
    }
}
