package com.enonic.xp.lib.auth;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.PrincipalRelationship;
import com.enonic.xp.security.PrincipalRelationships;
import com.enonic.xp.security.Principals;
import com.enonic.xp.security.SecurityService;

public final class GetMembersHandler
    implements ScriptBean
{
    private PrincipalKey principalKey;

    private Supplier<SecurityService> securityService;

    public void setPrincipalKey( final String value )
    {
        this.principalKey = value == null ? null : PrincipalKey.from( value );
    }

    public List<PrincipalMapper> getMembers()
    {
        final PrincipalRelationships relationships = this.securityService.get().getRelationships( this.principalKey );
        final PrincipalKeys principalKeys = PrincipalKeys.from( relationships.stream().
            map( PrincipalRelationship::getTo ).
            toArray( PrincipalKey[]::new ) );
        final Principals principals = this.securityService.get().getPrincipals( principalKeys );
        return principals.stream().map( PrincipalMapper::new ).collect( Collectors.toList() );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.securityService = context.getService( SecurityService.class );
    }
}
