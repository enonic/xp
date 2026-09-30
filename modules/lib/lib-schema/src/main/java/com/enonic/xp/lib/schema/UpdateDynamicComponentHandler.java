package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.lib.schema.mapper.DescriptorConverter;
import com.enonic.xp.region.ComponentDescriptor;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.DynamicSchemaService;
import com.enonic.xp.resource.UpdateDynamicComponentParams;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class UpdateDynamicComponentHandler
    implements ScriptBean
{
    private String key;

    private String type;

    private String resource;

    private Supplier<DynamicSchemaService> dynamicSchemaServiceSupplier;

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
        final DynamicSchemaService service = dynamicSchemaServiceSupplier.get();
        final UpdateDynamicComponentParams params =
            UpdateDynamicComponentParams.create().descriptorKey( DescriptorKey.from( key ) ).resource( resource ).build();

        final DynamicSchemaResult<? extends ComponentDescriptor> result = switch ( type )
        {
            case "PART" -> service.updatePart( params );
            case "LAYOUT" -> service.updateLayout( params );
            case "PAGE" -> service.updatePage( params );
            default -> throw new IllegalArgumentException( "illegal component type: " + type );
        };

        return DescriptorConverter.convert( result );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        dynamicSchemaServiceSupplier = context.getService( DynamicSchemaService.class );
    }
}
