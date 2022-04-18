package com.enonic.xp.lib.app;

import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.enonic.xp.app.ApplicationDescriptorService;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.lib.app.mapper.ApplicationMapper;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class ListApplicationsHandler
    implements ScriptBean
{
    private Supplier<ApplicationService> applicationServiceSupplier;

    private Supplier<ApplicationDescriptorService> applicationDescriptorServiceSupplier;


    public Object execute()
    {
        return applicationServiceSupplier.get()
            .list()
            .stream()
            .map( app -> new ApplicationMapper( app, applicationDescriptorServiceSupplier.get().get( app.getKey() ) ) )
            .collect( Collectors.toList() );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        applicationServiceSupplier = context.getService( ApplicationService.class );
        applicationDescriptorServiceSupplier = context.getService( ApplicationDescriptorService.class );
    }
}
