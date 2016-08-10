package com.enonic.xp.lib.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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

public final class ModifyProfileHandler
    implements ScriptBean
{
    private Supplier<SecurityService> securityService;

    private PrincipalKey key;

    private String scope;

    private ScriptValue editor;

    public void setKey( final String key )
    {
        this.key = PrincipalKey.from( key );
    }

    public void setScope( final String scope )
    {
        this.scope = scope;
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
                editor( this.newProfileEditor() ).
                build();

            final User updatedUser = this.securityService.get().updateUser( params );
            final PropertyTree updatedProfile = updatedUser.getProfile();
            return createPropertyMapper( updatedProfile );
        }

        return null;
    }

    private UserEditor newProfileEditor()
    {
        return edit -> {
            final PropertyTree profile = edit.source.getProfile();
            final PropertyTreeMapper mapper = createPropertyMapper( profile );
            final ScriptValue scriptValue = this.editor.call( mapper );
            updateUser( edit, getValue( scriptValue ) );
        };
    }

    private Object getValue( ScriptValue scriptValue )
    {
        if ( scriptValue == null )
        {
            return null;
        }
        if ( scriptValue.isObject() )
        {
            return scriptValue.getMap();
        }
        if ( scriptValue.isArray() )
        {
            return scriptValue.getArray();
        }
        return scriptValue.getValue();
    }


    private PropertyTreeMapper createPropertyMapper( PropertyTree profile )
    {
        if ( profile == null )
        {
            return null;
        }

        if ( this.scope == null )
        {
            return new PropertyTreeMapper( profile );
        }
        else
        {
            //TODO
            //final Property property = profile.getProperty( this.scope );
            return new PropertyTreeMapper( profile );
        }
    }

    private void updateUser( final EditableUser target, final Object value )
    {
        //TODO Temporary fix
        final HashMap<String, Object> map = new HashMap<>();
        map.put( "tmp", value );

        final PropertyTree propertyTree = createPropertyTree( map );

        //TODO Temporary fix
        target.profile = propertyTree.getSet( "tmp" ).toTree();
        //target.profile.setProperty( scope, propertyTree.getProperty( "tmp" ).getValue() );
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
