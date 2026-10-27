package com.enonic.xp.lib.auth;

import java.util.List;
import java.util.function.Supplier;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.SecurityService;

@NullMarked
public final class GetScopesHandler
    implements ScriptBean
{
    private Supplier<SecurityService> securityService;

    private String idProvider;

    public void setIdProvider( final String idProvider )
    {
        this.idProvider = idProvider;
    }

    public List<ScopeMapper> getScopes()
    {
        return securityService.get().getScopes( IdProviderKey.from( idProvider ) ).stream().map( ScopeMapper::new ).toList();
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.securityService = context.getService( SecurityService.class );
    }
}
