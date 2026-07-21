package com.enonic.xp.lib.schema;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.lib.schema.mapper.SchemaConverter;
import com.enonic.xp.lib.schema.mapper.SchemaMapper;
import com.enonic.xp.resource.DynamicContentSchemaType;
import com.enonic.xp.resource.SchemaService;
import com.enonic.xp.resource.ListDynamicContentSchemasParams;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class ListDynamicSchemasHandler
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

    public List<SchemaMapper> execute()
    {
        final ListDynamicContentSchemasParams params = ListDynamicContentSchemasParams.create()
            .applicationKey( ApplicationKey.from( application ) )
            .type( DynamicContentSchemaType.valueOf( type ) )
            .build();

        return schemaServiceSupplier.get()
            .listContentSchemas( params )
            .stream()
            .map( SchemaConverter::convert )
            .collect( Collectors.toList() );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        schemaServiceSupplier = context.getService( SchemaService.class );
    }
}
