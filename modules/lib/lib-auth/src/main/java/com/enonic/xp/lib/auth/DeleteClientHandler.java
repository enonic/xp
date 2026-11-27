package com.enonic.xp.lib.auth;

import java.util.function.Supplier;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.SecurityService;

@NullMarked
public final class DeleteClientHandler
    implements ScriptBean
{
    private Supplier<SecurityService> securityService;

    private String idProvider;

    private String id;

    public void setIdProvider( final String idProvider )
    {
        this.idProvider = idProvider;
    }

    public void setId( final String id )
    {
        this.id = id;
    }

    public boolean deleteClient()
    {
        return securityService.get().deleteClient( IdProviderKey.from( idProvider ), id );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.securityService = context.getService( SecurityService.class );
    }
}
