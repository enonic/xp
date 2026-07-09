package com.enonic.xp.lib.schema;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.enonic.xp.lib.schema.mapper.NamespaceMapper;
import com.enonic.xp.schema.SchemaService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class ListNamespacesHandler
    implements ScriptBean
{
    private Supplier<SchemaService> schemaServiceSupplier;

    public List<NamespaceMapper> execute()
    {
        return schemaServiceSupplier.get()
            .listNamespaces()
            .stream()
            .map( NamespaceMapper::new )
            .collect( Collectors.toList() );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        schemaServiceSupplier = context.getService( SchemaService.class );
    }
}
