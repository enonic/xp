package com.enonic.wem.web.rest2.resource.account.role;

import java.util.Collection;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.web.rest2.common.JsonResult;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.UserEntity;

public final class RoleResult
    extends JsonResult
{
    public static final String TYPE_USER = "user";

    public static final String TYPE_GROUP = "group";

    public static final String TYPE_ROLE = "role";

    private final GroupEntity group;

    private final Collection<Object> members;

    public RoleResult( final GroupEntity group, final Collection<Object> members )
    {
        this.group = group;
        this.members = members;
    }

    @Override
    public JsonNode toJson()
    {
        final ObjectNode json = objectNode();

        boolean builtIn = group.isBuiltIn();
        boolean isAuth = GroupType.AUTHENTICATED_USERS.equals( group.getType() );
        boolean isAnonym = GroupType.ANONYMOUS.equals( group.getType() );

        json.put( "key", String.valueOf( group.getGroupKey() ) );
        json.put( "type", TYPE_ROLE );
        json.put( "name", group.getName() );
        json.put( "userStore", group.getUserStore() != null ? group.getUserStore().getName() : "null" );
        json.put( "qualifiedName", String.valueOf( group.getQualifiedName() ) );
        json.put( "displayName", group.getName() ); // TODO
        json.put( "description", group.getDescription() );
        json.put( "lastModified", "2012-07-24 16:18:35" ); // TODO
        json.put( "created", "2012-07-24 16:18:35" ); // TODO
        json.put( "builtIn", builtIn );
        json.put( "editable", !( isAuth || isAnonym ) );

        json.put( "members", toJson( members ) );

        return json;
    }

    private JsonNode toJson( final Collection members )
    {
        final ArrayNode jsonArray = arrayNode();
        for ( Object member : members )
        {
            final ObjectNode json = objectNode();

            if ( member instanceof UserEntity )
            {
                final UserEntity user = (UserEntity) member;
                json.put( "name", user.getName() );
                json.put( "qualifiedName", user.getQualifiedName() != null ? user.getQualifiedName().toString() : null );
                json.put( "displayName", user.getDisplayName() );
                json.put( "type", TYPE_USER );
                json.put( "key", user.getKey().toString() );
            }
            else
            {
                final GroupEntity group = (GroupEntity) member;
                final String type = group.isBuiltIn() ? TYPE_ROLE : TYPE_GROUP;
                json.put( "name", group.getName() );
                json.put( "qualifiedName", group.getQualifiedName() != null ? group.getQualifiedName().toString() : null );
                json.put( "displayName", group.getName() );
                json.put( "type", type );
                json.put( "key", group.getGroupKey().toString() );
            }

            jsonArray.add( json );
        }
        return jsonArray;
    }
}
