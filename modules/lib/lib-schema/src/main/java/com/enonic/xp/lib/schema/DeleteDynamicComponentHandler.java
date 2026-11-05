package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.enonic.xp.descriptor.DescriptorKey;
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
        final DynamicSchemaService service = dynamicSchemaServiceSupplier.get();
        final DescriptorKey descriptorKey = DescriptorKey.from( key );

        return switch ( type )
        {
            case "PART" -> service.deletePart( descriptorKey );
            case "LAYOUT" -> service.deleteLayout( descriptorKey );
            case "PAGE" -> service.deletePage( descriptorKey );
            default -> throw new IllegalArgumentException( "illegal component type: " + type );
        };
    }

    @Override
    public void initialize( final BeanContext context )
    {
        dynamicSchemaServiceSupplier = context.getService( DynamicSchemaService.class );
    }
}
