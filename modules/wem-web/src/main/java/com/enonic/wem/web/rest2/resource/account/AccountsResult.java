package com.enonic.wem.web.rest2.resource.account;

import java.util.Collection;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.account.AccountType;
import com.enonic.wem.core.search.Facet;
import com.enonic.wem.core.search.FacetEntry;
import com.enonic.wem.core.search.Facets;
import com.enonic.wem.web.rest2.common.JsonResult;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.UserStoreEntity;

public final class AccountsResult
    extends JsonResult
{

    public static final String USERSTORE_SYSTEM = "system";

    public static final String TYPE_USER = "user";

    public static final String TYPE_GROUP = "group";

    public static final String TYPE_ROLE = "role";

    private final Collection results;

    private final Facets facets;

    private final int total;

    public AccountsResult( final Collection results, final Facets facets, final int total )
    {
        this.results = results;
        this.facets = facets;
        this.total = total;
    }

    @Override
    public JsonNode toJson()
    {
        final ObjectNode json = objectNode();

        json.put( "total", this.total );

        final ArrayNode accounts = json.putArray( "accounts" );
        for ( final Object result : this.results )
        {
            if ( result instanceof UserEntity )
            {
                accounts.add( toJson( (UserEntity) result ) );
            }
            else if ( result instanceof GroupEntity )
            {
                accounts.add( toJson( (GroupEntity) result ) );
            }
        }

        final ArrayNode facets = json.putArray( "facets" );
        for ( Facet facet : this.facets )
        {
            facets.add( toJson( facet ) );
        }

        return json;
    }

    private ObjectNode toJson( UserEntity user )
    {
        final ObjectNode json = objectNode();

        final UserStoreEntity userstore = user.getUserStore();
        final boolean isAdmin = userstore != null && user.isEnterpriseAdmin() && USERSTORE_SYSTEM.equals( userstore.getName() );
        final boolean isAnonym = user.isAnonymous();
        final String key = String.valueOf( user.getKey() );

        json.put( "key", key );
        json.put( "new_key", composeNewKey( user ) );
        json.put( "type", TYPE_USER );
        json.put( "name", user.getName() );
        json.put( "email", user.getEmail() );
        json.put( "userStore", userstore != null ? userstore.getName() : "null" );
        json.put( "qualifiedName", String.valueOf( user.getQualifiedName() ) );
        json.put( "displayName", user.getDisplayName() );
        json.put( "lastModified", "2011-08-09 08:23:07" );          //TODO
        json.put( "created", "2011-08-09 08:23:07" );           //TODO
        json.put( "builtIn", user.isBuiltIn() );
        json.put( "editable", !( isAnonym || isAdmin ) );
        json.put( "info_uri", AccountUriHelper.getAccountInfoUri( AccountType.USER, key ) );
        json.put( "image_uri", AccountUriHelper.getAccountImageUri( user ) );
        json.put( "graph_uri", AccountUriHelper.getAccountGraphUri( key ) );
        return json;
    }

    private ObjectNode toJson( GroupEntity group )
    {
        final ObjectNode json = objectNode();

        final boolean builtIn = group.isBuiltIn();
        final boolean isAuth = GroupType.AUTHENTICATED_USERS.equals( group.getType() );
        final boolean isAnonym = GroupType.ANONYMOUS.equals( group.getType() );
        final String key = String.valueOf( group.getGroupKey() );

        json.put( "key", key );
        json.put( "new_key", composeNewKey( group ) );
        json.put( "type", builtIn ? TYPE_ROLE : TYPE_GROUP );
        json.put( "name", group.getName() );
        json.put( "userStore", group.getUserStore() != null ? group.getUserStore().getName() : "null" );
        json.put( "qualifiedName", String.valueOf( group.getQualifiedName() ) );
        json.put( "displayName", group.getName() );        //TODO: temporary solution
        json.put( "lastModified", "2012-07-24 16:18:35" );          //TODO
        json.put( "created", "2012-07-24 16:18:35" );           //TODO
        json.put( "builtIn", builtIn );
        json.put( "editable", !( isAuth || isAnonym ) );

        final AccountType accountType = builtIn ? AccountType.ROLE : AccountType.GROUP;
        json.put( "image_uri", AccountUriHelper.getAccountImageUri( group ) );
        json.put( "info_uri", AccountUriHelper.getAccountInfoUri( accountType, key ) );
        json.put( "graph_uri", AccountUriHelper.getAccountGraphUri( key ) );

        return json;
    }

    private ObjectNode toJson( Facet facet )
    {
        final ObjectNode json = objectNode();

        json.put( "name", facet.getName() );
        ObjectNode terms = json.putObject( "terms" );
        for ( FacetEntry facetEntry : facet )
        {
            terms.put( facetEntry.getTerm(), facetEntry.getCount() );
        }

        return json;
    }

    private String composeNewKey( final UserEntity entity )
    {
        return composeNewKey( "user", entity.getUserStore(), entity.getName() );
    }

    private String composeNewKey( final GroupEntity entity )
    {
        final String type = entity.isBuiltIn() ? TYPE_ROLE : TYPE_GROUP;
        return composeNewKey( type, entity.getUserStore(), entity.getName() );
    }

    private String composeNewKey( final String type, final UserStoreEntity userStore, final String localName )
    {
        final StringBuilder str = new StringBuilder( type );
        str.append( ":" );

        if ( userStore != null )
        {
            str.append( userStore.getName() );
        }
        else
        {
            str.append( "system" );
        }

        str.append( ":" ).append( localName );
        return str.toString();
    }
}
