package com.enonic.xp.lib.schema;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.enonic.xp.lib.schema.mapper.ApplicationMapper;
import com.enonic.xp.resource.SchemaService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class ListApplicationsHandler
    implements ScriptBean
{
    private Supplier<SchemaService> schemaServiceSupplier;


    public List<ApplicationMapper> execute()
    {
        return schemaServiceSupplier.get().list().stream().map( ApplicationMapper::new ).collect( Collectors.toList() );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        schemaServiceSupplier = context.getService( SchemaService.class );
    }
}
