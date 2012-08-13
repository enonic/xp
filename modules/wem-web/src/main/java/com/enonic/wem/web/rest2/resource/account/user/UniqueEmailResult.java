package com.enonic.wem.web.rest2.resource.account.user;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.web.rest2.common.JsonResult;

public final class UniqueEmailResult
    extends JsonResult
{
    private final boolean emailInUse;

    private final String userkey;

    public UniqueEmailResult( final boolean emailInUse, final String userkey )
    {
        this.emailInUse = emailInUse;
        this.userkey = userkey;
    }

    public UniqueEmailResult( final boolean emailInUse )
    {
        this.emailInUse = emailInUse;
        this.userkey = null;
    }

    @Override
    public JsonNode toJson()
    {
        final ObjectNode json = objectNode();
        json.put( "emailInUse", emailInUse );
        json.put( "userkey", userkey );
        return json;
    }

    public boolean isEmailInUse()
    {
        return emailInUse;
    }

    public String getUserkey()
    {
        return userkey;
    }

}
