package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.enonic.xp.lib.schema.mapper.DescriptorConverter;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.resource.DynamicComponentType;
import com.enonic.xp.resource.SchemaService;
import com.enonic.xp.resource.UpdateDynamicComponentParams;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class UpdateDynamicComponentHandler
    implements ScriptBean
{
    private String key;

    private String type;

    private String resource;

    private Supplier<SchemaService> schemaServiceSupplier;

    public void setKey( final String key )
    {
        this.key = key;
    }

    public void setType( final String type )
    {
        this.type = type;
    }

    public void setResource( final String resource )
    {
        this.resource = resource;
    }

    public Object execute()
    {
        final UpdateDynamicComponentParams params = UpdateDynamicComponentParams.create()
            .descriptorKey( DescriptorKey.from( key ) )
            .type( DynamicComponentType.valueOf( type ) )
            .resource( resource )
            .build();

        return DescriptorConverter.convert( schemaServiceSupplier.get().updateComponent( params ) );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        schemaServiceSupplier = context.getService( SchemaService.class );
    }
}
