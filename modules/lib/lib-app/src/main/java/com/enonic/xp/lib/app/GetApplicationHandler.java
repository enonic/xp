package com.enonic.xp.lib.app;

import java.util.Optional;
import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.lib.app.mapper.ApplicationMapper;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class GetApplicationHandler
    implements ScriptBean
{
    private String key;

    private Supplier<ApplicationService> applicationServiceSupplier;


    public ApplicationMapper execute()
    {
        return Optional.ofNullable( applicationServiceSupplier.get().get( ApplicationKey.from( key ) ) )
            .map( ApplicationMapper::new )
            .orElse( null );
    }

    public void setKey( final String key )
    {
        this.key = key;
    }


    @Override
    public void initialize( final BeanContext context )
    {
        applicationServiceSupplier = context.getService( ApplicationService.class );
    }
}
