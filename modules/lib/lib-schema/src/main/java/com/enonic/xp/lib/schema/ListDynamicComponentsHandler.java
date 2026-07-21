package com.enonic.xp.lib.schema;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.lib.schema.mapper.DescriptorConverter;
import com.enonic.xp.lib.schema.mapper.DescriptorMapper;
import com.enonic.xp.resource.DynamicComponentType;
import com.enonic.xp.resource.SchemaService;
import com.enonic.xp.resource.ListDynamicComponentsParams;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class ListDynamicComponentsHandler
    implements ScriptBean
{
    private String application;

    private String type;

    private Supplier<SchemaService> schemaServiceSupplier;

    public void setApplication( final String application )
    {
        this.application = application;
    }

    public void setType( final String type )
    {
        this.type = type;
    }

    public List<DescriptorMapper> execute()
    {
        final ListDynamicComponentsParams params = ListDynamicComponentsParams.create()
            .applicationKey( ApplicationKey.from( application ) )
            .type( DynamicComponentType.valueOf( type ) )
            .build();

        return schemaServiceSupplier.get()
            .listComponents( params )
            .stream()
            .map( DescriptorConverter::convert )
            .map( o -> o )
            .collect( Collectors.toList() );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        schemaServiceSupplier = context.getService( SchemaService.class );
    }
}
