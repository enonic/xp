package com.enonic.xp.lib.auth;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import com.enonic.xp.convert.Converters;
import com.enonic.xp.lib.common.PrincipalMapper;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.security.EditableUser;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UpdateUserParams;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserEditor;

public final class ModifyUserHandler
    extends PrincipalHandler
{
    private Supplier<SecurityService> securityService;

    private ScriptValue editor;

    private PrincipalKey principalKey;

    public void setEditor( final ScriptValue editor )
    {
        this.editor = editor;
    }

    public void setPrincipalKey( final String principalKey )
    {
        if ( principalKey == null )
        {
            this.principalKey = null;
        }
        else
        {
            this.principalKey = PrincipalKey.from( principalKey );
        }
    }

    public PrincipalMapper modifyUser()
    {
        final Optional<User> existingUser = this.securityService.get().getUser( principalKey );

        if ( existingUser.isPresent() )
        {
            final UpdateUserParams params = UpdateUserParams.create().
                userKey( this.principalKey ).
                editor( this.newUserEditor() ).
                build();

            return new PrincipalMapper( this.securityService.get().updateUser( params ) );
        }
        return null;
    }

    private UserEditor newUserEditor()
    {
        return edit -> {
            final ScriptValue value = this.editor.call( new PrincipalMapper( edit.source ) );
            if ( value != null )
            {
                updateUser( edit, value.getMap() );
            }
        };
    }

    private void updateUser( final EditableUser target, final Map map )
    {
        final String displayName = Converters.convert( map.get( "displayName" ), String.class );
        if ( displayName != null )
        {
            target.displayName = trim( displayName );
        }

        final String email = Converters.convert( map.get( "email" ), String.class );
        if ( email != null )
        {
            target.email = trim( email );
        }
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.securityService = context.getService( SecurityService.class );
    }
}
