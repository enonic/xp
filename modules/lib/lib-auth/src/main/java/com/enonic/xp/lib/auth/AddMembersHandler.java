package com.enonic.xp.lib.auth;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalRelationship;
import com.enonic.xp.security.SecurityService;

public final class AddMembersHandler
    implements ScriptBean
{
    private PrincipalKey principalKey;

    private Supplier<SecurityService> securityService;

    private PrincipalKey[] members;

    public void setPrincipalKey( final String value )
    {
        this.principalKey = value == null ? null : PrincipalKey.from( value );
    }

    public void setMembers( final String[] values )
    {
        this.members = Stream.of( values ).filter( Objects::nonNull ).map( PrincipalKey::from ).toArray( PrincipalKey[]::new );
    }

    public void addMembers()
    {
        for ( final PrincipalKey key : this.members )
        {
            this.securityService.get().addRelationship( PrincipalRelationship.from( this.principalKey ).to( key ) );
        }
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.securityService = context.getService( SecurityService.class );
    }
}
