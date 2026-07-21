package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.resource.DeleteDynamicComponentParams;
import com.enonic.xp.resource.DynamicComponentType;
import com.enonic.xp.resource.SchemaService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class DeleteDynamicComponentHandler
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

    public boolean execute()
    {
        final DynamicComponentType dynamicComponentType = DynamicComponentType.valueOf( type );
        final DescriptorKey descriptorKey = DescriptorKey.from( key );

        final DeleteDynamicComponentParams params =
            DeleteDynamicComponentParams.create().descriptorKey( descriptorKey ).type( dynamicComponentType ).build();

        return schemaServiceSupplier.get().deleteComponent( params );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        schemaServiceSupplier = context.getService( SchemaService.class );
    }
}
