package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.enonic.xp.lib.schema.mapper.DescriptorConverter;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.region.ComponentDescriptor;
import com.enonic.xp.schema.SchemaResult;
import com.enonic.xp.schema.SchemaService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class GetComponentHandler
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
        final DescriptorKey descriptorKey = DescriptorKey.from( key );

        final SchemaResult<? extends ComponentDescriptor> component = switch ( type )
        {
            case "PART" -> schemaServiceSupplier.get().getPart( descriptorKey );
            case "LAYOUT" -> schemaServiceSupplier.get().getLayout( descriptorKey );
            case "PAGE" -> schemaServiceSupplier.get().getPage( descriptorKey );
            default -> throw new IllegalArgumentException( "unknown component type: " + type );
        };

        return component != null ? DescriptorConverter.convert( component ) : null;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        schemaServiceSupplier = context.getService( SchemaService.class );
    }
}
