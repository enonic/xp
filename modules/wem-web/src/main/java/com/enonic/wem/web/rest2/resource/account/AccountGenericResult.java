package com.enonic.wem.web.rest2.resource.account;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.web.rest2.common.JsonResult;

public class AccountGenericResult
    extends JsonResult
{
    private final boolean success;

    private final String error;


    public AccountGenericResult( final boolean success, final String errorMessage )
    {
        this.success = success;
        this.error = errorMessage;
    }

    public AccountGenericResult( final boolean success )
    {
        this.success = success;
        this.error = null;
    }

    @Override
    public JsonNode toJson()
    {
        final ObjectNode json = objectNode();
        json.put( "success", success );
        if ( error != null )
        {
            json.put( "error", error );
        }
        return json;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public String getError()
    {
        return error;
    }
}
