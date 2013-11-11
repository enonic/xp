package com.enonic.wem.admin.rest.resource.content.json;

import com.enonic.wem.admin.json.content.ContentJson;
import com.enonic.wem.admin.rest.resource.ErrorJson;

public class CreateOrUpdateContentJsonResult
{
    private ErrorJson error;

    private ContentJson result;

    public ErrorJson getError()
    {
        return error;
    }

    public void setError( final ErrorJson error )
    {
        this.error = error;
    }

    public ContentJson getResult()
    {
        return result;
    }

    public void setResult( final ContentJson result )
    {
        this.result = result;
    }

    public static CreateOrUpdateContentJsonResult error(String message) {
        final CreateOrUpdateContentJsonResult result = new CreateOrUpdateContentJsonResult();
        result.setError( new ErrorJson( message ) );
        return result;
    }

    public static CreateOrUpdateContentJsonResult result(ContentJson content) {
        final CreateOrUpdateContentJsonResult result = new CreateOrUpdateContentJsonResult();
        result.setResult( content );
        return result;
    }
}

