package com.enonic.xp.lib.auth;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.osgi.service.component.annotations.Component;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.context.Context;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.Principal;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.Principals;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.auth.AuthenticationInfo;

@Component(immediate = true)
public final class GetMembershipsHandler
    implements ScriptBean
{
    private Supplier<Context> context;

    private PrincipalKey principalKey;

    private Supplier<SecurityService> securityService;

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

    public List<PrincipalMapper> getMemberships()
    {
        final AuthenticationInfo authInfo = this.context.get().getAuthInfo();
        if ( authInfo.isAuthenticated() )
        {
            final PrincipalKeys principalKeys = this.securityService.get().getMemberships( this.principalKey );
            final Principals principals = this.securityService.get().getPrincipals( principalKeys );
            final ImmutableList.Builder<PrincipalMapper> principalMappers = ImmutableList.builder();
            for ( final Principal principal : principals )
            {
                principalMappers.add( new PrincipalMapper( principal ) );
            }
            return principalMappers.build();
        }
        else
        {
            return Collections.emptyList();
        }
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.context = context.getBinding( Context.class );
        this.securityService = context.getService( SecurityService.class );
    }
}
