package com.enonic.wem.admin.rpc.account;

import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;
import com.enonic.wem.admin.rest.resource.account.AccountImageUriResolver;
import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.core.support.serializer.JsonSerializerUtil;

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
        JsonSerializerUtil.setDateTimeValue( "modifiedTime", account.getModifiedTime(), json );
        JsonSerializerUtil.setDateTimeValue( "createdTime", account.getCreatedTime(), json );
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
