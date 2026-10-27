package com.enonic.xp.lib.auth;

import java.util.List;
import java.util.function.Supplier;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.SecurityService;

@NullMarked
public final class GetClientsHandler
    implements ScriptBean
{
    private Supplier<SecurityService> securityService;

    private String idProvider;

    public void setIdProvider( final String idProvider )
    {
        this.idProvider = idProvider;
    }

    public List<ClientMapper> getClients()
    {
        return securityService.get().getClients( IdProviderKey.from( idProvider ) ).stream().map( ClientMapper::new ).toList();
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.securityService = context.getService( SecurityService.class );
    }
}
