package com.enonic.wem.admin.rest.resource.content.json;


import com.enonic.wem.api.command.content.UpdateContentResult;

public class UpdateContentJson
{
    private boolean updated;

    private String failure;

    public UpdateContentJson( final UpdateContentResult result )
    {
        if ( result != null && result != UpdateContentResult.SUCCESS )
        {
            failure = result.getMessage();
        }
        updated = result != null && result == UpdateContentResult.SUCCESS;
    }

    public UpdateContentJson( final Throwable e )
    {
        failure = e.getMessage();
        updated = false;
    }

    public boolean isUpdated()
    {
        return updated;
    }

    public String getFailure()
    {
        return failure;
    }
}
