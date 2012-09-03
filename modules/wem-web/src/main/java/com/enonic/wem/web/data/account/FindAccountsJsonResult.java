package com.enonic.wem.web.data.account;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.RoleAccount;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.api.account.result.AccountFacet;
import com.enonic.wem.api.account.result.AccountFacetEntry;
import com.enonic.wem.api.account.result.AccountResult;
import com.enonic.wem.web.json.result.JsonSuccessResult;

final class FindAccountsJsonResult
    extends JsonSuccessResult
{
    private final AccountResult result;

    public FindAccountsJsonResult( final AccountResult result )
    {
        this.result = result;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "total", this.result.getTotalSize() );

        final ArrayNode accounts = json.putArray( "accounts" );
        for ( final Account account : this.result )
        {
            serializeAccount( accounts.addObject(), account );
        }

        final ArrayNode facets = json.putArray( "facets" );
        for ( final AccountFacet facet : this.result.getFacets() )
        {
            serializeFacet( facets.addObject(), facet );
        }
    }

    private void serializeFacet( final ObjectNode json, final AccountFacet facet )
    {
        json.put( "name", facet.getName() );

        final ObjectNode terms = json.putObject( "terms" );
        for ( final AccountFacetEntry facetEntry : facet )
        {
            terms.put( facetEntry.getTerm(), facetEntry.getCount() );
        }
    }

    private void serializeAccount( final ObjectNode json, final Account account )
    {
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
        json.put( "image_url", getImageUrl( account ) );

        if ( account instanceof UserAccount )
        {
            serializeUser( json, (UserAccount) account );
        }
    }

    private void serializeUser( final ObjectNode json, final UserAccount account )
    {
        json.put( "email", account.getEmail() );
    }

    private String getImageUrl( final Account account )
    {
        if ( account instanceof UserAccount )
        {
            return getImageUrl( (UserAccount) account );
        }

        if ( account instanceof RoleAccount )
        {
            return buildImageUrl( "default/group" );
        }

        return buildImageUrl( "default/role" );
    }

    private String getImageUrl( final UserAccount account )
    {
        if ( account.getKey().isAnonymous() )
        {
            return buildImageUrl( "default/anonymous" );
        }

        if ( account.getKey().isSuperUser() )
        {
            return buildImageUrl( "default/admin" );
        }

        if ( account.getImage() != null )
        {
            return buildImageUrl( account.getKey().toString() );
        }
        else
        {
            return buildImageUrl( "default/user" );
        }
    }

    private String buildImageUrl( final String path )
    {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path( "admin/rest/binary/account/image/" ).path(
            path ).build().toString();
    }
}
