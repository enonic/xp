package com.enonic.xp.lib.app;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.lib.app.mapper.ApplicationMapper;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class ListApplicationsHandler
    implements ScriptBean
{
    private Supplier<ApplicationService> applicationServiceSupplier;


    public List<ApplicationMapper> execute()
    {
        return applicationServiceSupplier.get().list().stream().map( ApplicationMapper::new ).collect( Collectors.toList() );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        applicationServiceSupplier = context.getService( ApplicationService.class );
    }
}
