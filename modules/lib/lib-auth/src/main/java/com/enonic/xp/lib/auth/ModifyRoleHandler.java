package com.enonic.xp.lib.auth;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import com.enonic.xp.convert.Converters;
import com.enonic.xp.lib.common.PrincipalMapper;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.EditableRole;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.Role;
import com.enonic.xp.security.RoleEditor;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UpdateRoleParams;

public final class ModifyRoleHandler
    implements ScriptBean
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

    public PrincipalMapper modifyRole()
    {
        final Optional<Role> existingRole = this.securityService.get().getRole( principalKey );

        if ( existingRole.isPresent() )
        {
            final UpdateRoleParams params = UpdateRoleParams.create().
                roleKey( this.principalKey ).
                editor( this.newRoleEditor() ).
                build();

            return new PrincipalMapper( this.securityService.get().updateRole( params ) );
        }
        return null;
    }

    private RoleEditor newRoleEditor()
    {
        return edit -> {
            final ScriptValue value = this.editor.call( new PrincipalMapper( edit.source ) );
            if ( value != null )
            {
                updateRole( edit, value.getMap() );
            }
        };
    }

    private void updateRole( final EditableRole target, final Map map )
    {
        final String displayName = Converters.convert( map.get( "displayName" ), String.class );
        if ( displayName != null )
        {
            target.displayName = displayName;
        }

        final String description = Converters.convert( map.get( "description" ), String.class );
        if ( description != null )
        {
            target.description = description;
        }
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.securityService = context.getService( SecurityService.class );
    }
}
