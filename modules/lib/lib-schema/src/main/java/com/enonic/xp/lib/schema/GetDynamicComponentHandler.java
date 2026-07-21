package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.enonic.xp.lib.schema.mapper.DescriptorConverter;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.resource.DynamicComponentType;
import com.enonic.xp.resource.SchemaService;
import com.enonic.xp.resource.GetDynamicComponentParams;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class GetDynamicComponentHandler
    implements ScriptBean
{
    private String key;

    private String type;

    private Supplier<SchemaService> schemaServiceSupplier;

    public void setKey( final String key )
    {
        this.key = key;
    }

    public void setType( final String type )
    {
        this.type = type;
    }

    public Object execute()
    {
        final GetDynamicComponentParams params = GetDynamicComponentParams.create()
            .descriptorKey( DescriptorKey.from( key ) )
            .type( DynamicComponentType.valueOf( type ) )
            .build();

        return DescriptorConverter.convert( schemaServiceSupplier.get().getComponent( params ) );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        schemaServiceSupplier = context.getService( SchemaService.class );
    }
}
