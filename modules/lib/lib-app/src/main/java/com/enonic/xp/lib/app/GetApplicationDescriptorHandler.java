package com.enonic.xp.lib.app;

import java.util.Optional;
import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationDescriptorService;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.lib.app.mapper.ApplicationDescriptorMapper;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class GetApplicationDescriptorHandler
    implements ScriptBean
{
    private String key;

    private Supplier<ApplicationDescriptorService> applicationDescriptorServiceSupplier;


    public ApplicationDescriptorMapper execute()
    {
        return Optional.ofNullable( applicationDescriptorServiceSupplier.get().get( ApplicationKey.from( key ) ) )
            .map( ApplicationDescriptorMapper::new )
            .orElse( null );
    }

    public void setKey( final String key )
    {
        this.key = key;
    }


    @Override
    public void initialize( final BeanContext context )
    {
        applicationDescriptorServiceSupplier = context.getService( ApplicationDescriptorService.class );
    }
}
