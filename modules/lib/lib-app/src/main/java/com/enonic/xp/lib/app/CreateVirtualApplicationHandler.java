package com.enonic.xp.lib.app;

import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationDescriptorService;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.CreateVirtualApplicationParams;
import com.enonic.xp.lib.app.mapper.ApplicationMapper;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class CreateVirtualApplicationHandler
    implements ScriptBean
{
    private String key;

    private String displayName;

    private Supplier<ApplicationService> applicationServiceSupplier;

    private Supplier<ApplicationDescriptorService> applicationDescriptorServiceSupplier;

    public void setKey( final String key )
    {
        this.key = key;
    }

    public ApplicationMapper execute()
    {
        final ApplicationKey key = ApplicationKey.from( this.key );

        final CreateVirtualApplicationParams params = CreateVirtualApplicationParams.create().key( key ).build();

        return new ApplicationMapper( applicationServiceSupplier.get().createVirtualApplication( params ),
                                      applicationDescriptorServiceSupplier.get().get( key ) );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        applicationServiceSupplier = context.getService( ApplicationService.class );
        applicationDescriptorServiceSupplier = context.getService( ApplicationDescriptorService.class );
    }
}
