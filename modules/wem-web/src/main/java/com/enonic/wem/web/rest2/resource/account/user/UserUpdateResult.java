package com.enonic.wem.web.rest2.resource.account.user;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.web.rest2.resource.account.AccountGenericResult;

public class UserUpdateResult
    extends AccountGenericResult
{

    private String userKey;

    public UserUpdateResult( final boolean success, final String errorMessage, String userKey )
    {
        super( success, errorMessage );

        this.userKey = userKey;
    }

    public UserUpdateResult( final String userKey )
    {
        this( true, null, userKey );
    }

    public UserUpdateResult( final boolean success, final String errorMessage )
    {
        this( success, errorMessage, null );
    }

    @Override
    public JsonNode toJson()
    {
        ObjectNode node = (ObjectNode) super.toJson();
        if ( userKey != null )
        {
            node.put( "userKey", userKey );
        }

        return node;
    }
}
