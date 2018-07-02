package com.enonic.xp.lib.auth;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.lib.common.JsonToPropertyTreeTranslator;
import com.enonic.xp.lib.common.PropertyTreeMapper;
import com.enonic.xp.lib.value.ScriptValueTranslator;
import com.enonic.xp.lib.value.ScriptValueTranslatorResult;
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
            return createPropertyTreeMapper( updatedProfile, false );
        }

        return null;
    }

    private UserEditor newProfileEditor()
    {
        return edit -> {
            final PropertyTree profile = edit.source.getProfile();
            final PropertyTreeMapper mapper = createPropertyTreeMapper( profile, true );
            final ScriptValue scriptValue = this.editor.call( mapper );
            updateUser( edit, scriptValue );
        };
    }

    private PropertyTreeMapper createPropertyTreeMapper( PropertyTree profile, Boolean useRawValue )
    {
        if ( profile == null )
        {
            return null;
        }

        if ( this.scope == null )
        {
            return new PropertyTreeMapper( useRawValue, profile );
        }
        else
        {
            final PropertySet scopedProfile = profile.getSet( scope );
            return scopedProfile == null ? null : new PropertyTreeMapper( useRawValue, scopedProfile.toTree() );
        }
    }

    private void updateUser( final EditableUser target, final ScriptValue value )
    {
        if ( value == null )
        {
            if ( scope != null )
            {
                target.profile.removeProperty( scope );
            }
            return;
        }

        final ScriptValueTranslatorResult scriptValueTranslatorResult = new ScriptValueTranslator( false ).create( value );
        final PropertyTree propertyTree = scriptValueTranslatorResult.getPropertyTree();

        if ( this.scope == null )
        {
            target.profile = propertyTree;
        }
        else
        {
            target.profile.setSet( scope, propertyTree.getRoot() );
        }
    }

    private PropertyTree createPropertyTree( final Map<String, Object> value )
    {
        if ( value == null )
        {
            return null;
        }

        final JsonNode jsonNode = createJsonNode( value );
        return JsonToPropertyTreeTranslator.translate( jsonNode );
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
