package com.enonic.xp.lib.app;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class GetApplicationModeHandler
    implements ScriptBean
{
    private String key;

    private Supplier<ApplicationService> applicationServiceSupplier;


    public String execute()
    {
        return Optional.ofNullable( applicationServiceSupplier.get().getApplicationMode( ApplicationKey.from( key ) ) )
            .map( applicationMode -> applicationMode.toString().toLowerCase( Locale.ROOT ) )
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
