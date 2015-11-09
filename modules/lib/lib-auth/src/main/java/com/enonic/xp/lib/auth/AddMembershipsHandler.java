package com.enonic.xp.lib.auth;

import java.util.List;
import java.util.function.Supplier;

import com.enonic.xp.context.Context;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalRelationship;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.auth.AuthenticationInfo;

public final class AddMembershipsHandler
    implements ScriptBean
{
    private Supplier<Context> context;

    private PrincipalKey principalKey;

    private Supplier<SecurityService> securityService;

    private List<String> membershipsList;

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

    public void setMembershipsList( final ScriptValue value )
    {
        this.membershipsList = value != null ? value.getArray( String.class ) : null;
    }

    public void addMemberships()
    {
        final AuthenticationInfo authInfo = this.context.get().getAuthInfo();
        if ( authInfo.isAuthenticated() )
        {
            if ( this.membershipsList != null && this.principalKey != null )
            {
                for ( final String key : this.membershipsList )
                {
                    final PrincipalKey principalKeyToAdd = PrincipalKey.from( key );
                    this.securityService.get().addRelationship( PrincipalRelationship.from( principalKeyToAdd ).to( this.principalKey ) );
                }

            }
        }
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.context = context.getBinding( Context.class );
        this.securityService = context.getService( SecurityService.class );
    }
}
