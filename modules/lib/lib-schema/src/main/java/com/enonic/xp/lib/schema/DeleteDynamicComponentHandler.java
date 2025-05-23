package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.resource.DeleteDynamicComponentParams;
import com.enonic.xp.resource.DynamicComponentType;
import com.enonic.xp.resource.DynamicSchemaService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class DeleteDynamicComponentHandler
    implements ScriptBean
{
    private String key;

    private String type;

    private Supplier<DynamicSchemaService> dynamicSchemaServiceSupplier;

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

        return dynamicSchemaServiceSupplier.get().deleteComponent( params );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        dynamicSchemaServiceSupplier = context.getService( DynamicSchemaService.class );
    }
}
