package com.enonic.wem.admin.rest.resource.schema.json;

import com.enonic.wem.admin.json.schema.SchemaJson;

public class CreateOrUpdateSchemaJsonResult
{

    private ErrorJson error;

    private SchemaJson result;

    public ErrorJson getError()
    {
        return error;
    }

    public void setError( final ErrorJson error )
    {
        this.error = error;
    }

    public SchemaJson getResult()
    {
        return result;
    }

    public void setResult( final SchemaJson result )
    {
        this.result = result;
    }

    public static CreateOrUpdateSchemaJsonResult error(final String message) {
        final CreateOrUpdateSchemaJsonResult result = new CreateOrUpdateSchemaJsonResult();
        result.setError( new ErrorJson( message ) );
        return result;
    }

    public static CreateOrUpdateSchemaJsonResult result(final SchemaJson schemaJson) {
        final CreateOrUpdateSchemaJsonResult result = new CreateOrUpdateSchemaJsonResult();
        result.setResult( schemaJson );
        return result;
    }
}
