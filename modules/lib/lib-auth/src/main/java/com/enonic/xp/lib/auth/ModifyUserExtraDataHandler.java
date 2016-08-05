package com.enonic.xp.lib.auth;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.lib.content.mapper.JsonToPropertyTreeTranslator;
import com.enonic.xp.lib.content.mapper.PropertyTreeMapper;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.EditableUser;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UpdateUserParams;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserEditor;

public final class ModifyUserExtraDataHandler
    implements ScriptBean
{
    private Supplier<SecurityService> securityService;

    private PrincipalKey key;

    private String namespace;

    private ScriptValue editor;

    public void setKey( final String key )
    {
        this.key = PrincipalKey.from( key );
    }

    public void setNamespace( final String namespace )
    {
        this.namespace = namespace;
    }

    public void setEditor( final ScriptValue editor )
    {
        this.editor = editor;
    }

    public PropertyTreeMapper execute()
    {
        final Optional<User> user = this.securityService.get().
            getUser( this.key );

        if ( user.isPresent() )
        {
            final UpdateUserParams params = UpdateUserParams.create().
                userKey( this.key ).
                editor( this.newUserExtraDataEditor() ).
                build();

            final User updatedUser = this.securityService.get().updateUser( params );
            final PropertySet updatedUserExtraData = updatedUser.getExtraData( namespace );
            return updatedUserExtraData == null ? null : new PropertyTreeMapper( updatedUserExtraData.toTree() );
        }

        return null;
    }

    private UserEditor newUserExtraDataEditor()
    {
        return edit -> {
            final PropertySet extraData = edit.source.getExtraData( namespace );
            final PropertyTreeMapper mapper = extraData == null ? null : new PropertyTreeMapper( extraData.toTree() );
            final ScriptValue value = this.editor.call( mapper );
            if ( value != null )
            {
                updateUser( edit, value.getMap() );
            }
        };
    }

    private void updateUser( final EditableUser target, final Map<String, Object> map )
    {
        final PropertyTree propertyTree = createPropertyTree( map );
        target.extraDataMap.put( User.sanitizeNamespace( namespace ), propertyTree == null ? null : propertyTree.getRoot() );
    }

    private PropertyTree createPropertyTree( final Map<String, Object> value )
    {
        if ( value == null )
        {
            return null;
        }

        final JsonNode jsonNode = createJsonNode( value );
        return new JsonToPropertyTreeTranslator( null, false ).
            translate( jsonNode );
    }

    private JsonNode createJsonNode( final Map<String, Object> value )
    {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.valueToTree( value );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.securityService = context.getService( SecurityService.class );
    }
}
