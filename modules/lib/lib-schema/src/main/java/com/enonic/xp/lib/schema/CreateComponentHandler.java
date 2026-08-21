package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.enonic.xp.lib.schema.mapper.DescriptorConverter;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.region.ComponentDescriptor;
import com.enonic.xp.schema.CreateComponentParams;
import com.enonic.xp.schema.SchemaResult;
import com.enonic.xp.schema.SchemaService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class CreateComponentHandler
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
        final CreateComponentParams params = CreateComponentParams.create()
            .descriptorKey( DescriptorKey.from( key ) )
            .resource( resource )
            .build();

        final SchemaResult<? extends ComponentDescriptor> result = switch ( type )
        {
            case "PART" -> schemaServiceSupplier.get().createPart( params );
            case "LAYOUT" -> schemaServiceSupplier.get().createLayout( params );
            case "PAGE" -> schemaServiceSupplier.get().createPage( params );
            default -> throw new IllegalArgumentException( String.format( "unknown component type: \"%s\"", type ) );
        };

        return DescriptorConverter.convert( result );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        schemaServiceSupplier = context.getService( SchemaService.class );
    }
}
