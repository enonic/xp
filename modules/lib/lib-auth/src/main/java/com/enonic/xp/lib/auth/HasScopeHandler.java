package com.enonic.xp.lib.auth;

import java.util.function.Supplier;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.context.Context;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.auth.AccessTokenInfo;
import com.enonic.xp.security.token.ScopeService;

@NullMarked
public final class HasScopeHandler
    implements ScriptBean
{
    private Supplier<Context> context;

    private Supplier<ScopeService> scopeService;

    private String scope;

    public void setScope( final String scope )
    {
        this.scope = scope;
    }

    public boolean hasScope()
    {
        final AccessTokenInfo accessToken = this.context.get().getAuthInfo().getAccessToken();
        return accessToken == null || this.scopeService.get().getScopes().expand( accessToken.getScope() ).contains( this.scope );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.context = context.getBinding( Context.class );
        this.scopeService = context.getService( ScopeService.class );
    }
}
