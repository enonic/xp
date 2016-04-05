package com.enonic.xp.lib.auth;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import com.enonic.xp.convert.Converters;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.EditableGroup;
import com.enonic.xp.security.Group;
import com.enonic.xp.security.GroupEditor;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UpdateGroupParams;

public final class ModifyGroupHandler
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

    public PrincipalMapper modifyGroup()
    {
        final Optional<Group> existingGroup = this.securityService.get().getGroup( principalKey );

        if ( existingGroup.isPresent() )
        {
            final UpdateGroupParams params = UpdateGroupParams.create().
                groupKey( this.principalKey ).
                editor( this.newGroupEditor() ).
                build();

            return new PrincipalMapper( this.securityService.get().updateGroup( params ) );
        }
        return null;
    }

    private GroupEditor newGroupEditor()
    {
        return edit -> {
            final ScriptValue value = this.editor.call( new PrincipalMapper( edit.source ) );
            if ( value != null )
            {
                updateGroup( edit, value.getMap() );
            }
        };
    }

    private void updateGroup( final EditableGroup target, final Map map )
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
