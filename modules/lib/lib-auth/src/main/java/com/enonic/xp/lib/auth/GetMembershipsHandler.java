package com.enonic.xp.lib.auth;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.enonic.xp.lib.common.PrincipalMapper;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.Principals;
import com.enonic.xp.security.SecurityService;

public final class GetMembershipsHandler
    implements ScriptBean
{
    private PrincipalKey principalKey;

    private boolean transitive;

    private Supplier<SecurityService> securityService;

    public void setPrincipalKey( final String value )
    {
        this.principalKey = value == null ? null : PrincipalKey.from( value );
    }

    public void setTransitive( final Boolean transitive )
    {
        this.transitive = Boolean.TRUE.equals( transitive);
    }

    public List<PrincipalMapper> getMemberships()
    {
        final PrincipalKeys principalKeys;
        if ( transitive )
        {
            principalKeys = this.securityService.get().
                getAllMemberships( this.principalKey );
        }
        else
        {
            principalKeys = this.securityService.get().
                getMemberships( this.principalKey );
        }
        final Principals principals = this.securityService.get().
            getPrincipals( principalKeys );
        return principals.stream().
            map( PrincipalMapper::new ).
            collect( Collectors.toList() );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.securityService = context.getService( SecurityService.class );
    }
}
