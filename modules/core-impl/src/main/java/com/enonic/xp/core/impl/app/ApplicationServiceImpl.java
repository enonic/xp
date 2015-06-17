package com.enonic.xp.core.impl.app;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleService;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

@Component
public final class ApplicationServiceImpl
    implements ApplicationService
{
    private ModuleService moduleService;

    @Override
    public Applications getAll()
    {
        return this.moduleService.getAllModules().stream().
            filter( Module::isApplication ).
            map( Application::new ).
            collect( collectingAndThen( toList(), Applications::from ) );
    }

    @Override
    public Application getByKey( final ApplicationKey applicationKey )
    {
        final String appName = applicationKey.getName();

        final Module appModule = this.moduleService.getAllModules().stream().
            filter( ( module ) -> module.getKey().getName().equals( appName ) ).
            filter( Module::isApplication ).
            findFirst().
            orElse( null );

        return appModule != null ? new Application( appModule ) : null;
    }

    @Reference
    public void setModuleService( final ModuleService moduleService )
    {
        this.moduleService = moduleService;
    }
}
