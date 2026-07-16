package com.enonic.xp.lib.schema;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.lib.schema.mapper.NamespaceMapper;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class ListNamespacesHandler
    implements ScriptBean
{
    private Supplier<ApplicationService> applicationServiceSupplier;

    public List<NamespaceMapper> execute()
    {
        return applicationServiceSupplier.get()
            .listNamespaces()
            .stream()
            .map( namespace -> new NamespaceMapper( namespace.getKey(), namespace.getDescription() ) )
            .collect( Collectors.toList() );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        applicationServiceSupplier = context.getService( ApplicationService.class );
    }
}
