package com.enonic.xp.lib.auth;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.enonic.xp.lib.common.IdProviderMapper;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.SecurityService;

public final class GetIdProvidersHandler
    implements ScriptBean
{
    private Supplier<SecurityService> securityService;

    public List<IdProviderMapper> getIdProviders()
    {
        return this.securityService.get().getIdProviders().stream().map( IdProviderMapper::new ).collect( Collectors.toList() );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.securityService = context.getService( SecurityService.class );
    }
}
