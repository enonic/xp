package com.enonic.wem.web.rest2.resource.account.user;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.web.rest2.common.JsonResult;

public final class NameSuggestionResult
    extends JsonResult
{

    private final String username;

    public NameSuggestionResult( final String username )
    {
        this.username = username;
    }

    @Override
    public JsonNode toJson()
    {
        final ObjectNode json = objectNode();
        json.put( "username", username );
        return json;
    }

    public String getUsername()
    {
        return username;
    }

}
