package com.enonic.xp.core.impl.app;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.filter.AppFilter;

@Component(configurationPid = "com.enonic.xp.app")
public class AppFilterServiceImpl
    implements AppFilterService
{
    private final AppFilter appFilter;

    @Activate
    public AppFilterServiceImpl( final AppConfig config )
    {
        appFilter = new AppFilter( config.filter() );
    }

    @Override
    public boolean accept( final ApplicationKey key )
    {
        return appFilter.accept( key );
    }
}
