package com.enonic.wem.web.rest.rpc.account;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.web.rest.resource.account.AccountImageUriResolver;
import com.enonic.wem.web.json.JsonResult;

abstract class AbstractAccountJsonResult
    extends JsonResult
{
    public AbstractAccountJsonResult()
    {
        super( true );
    }

    protected void serializeAccount( ObjectNode json, Account account )
    {
        if ( json == null || account == null )
        {
            return;
        }
        json.put( "key", account.getKey().toString() );
        json.put( "type", account.getKey().getType().toString().toLowerCase() );
        json.put( "name", account.getKey().getLocalName() );
        json.put( "userStore", account.getKey().getUserStore() );
        json.put( "qualifiedName", account.getKey().getQualifiedName() );
        json.put( "builtIn", account.getKey().isBuiltIn() );
        json.put( "displayName", account.getDisplayName() );
        json.put( "modifiedTime", account.getModifiedTime().toString() );
        json.put( "createdTime", account.getCreatedTime().toString() );
        json.put( "editable", account.isEditable() );
        json.put( "deleted", account.isDeleted() );
        json.put( "image_url", AccountImageUriResolver.resolve( account ) );

        if ( account instanceof UserAccount )
        {
            serializeUser( json, (UserAccount) account );
        }
    }

    private void serializeUser( final ObjectNode json, final UserAccount account )
    {
        json.put( "email", account.getEmail() );
    }
}
