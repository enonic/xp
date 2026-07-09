package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.schema.SchemaService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class DeleteComponentHandler
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
        final DescriptorKey descriptorKey = DescriptorKey.from( key );

        return switch ( type )
        {
            case "PART" -> schemaServiceSupplier.get().deletePart( descriptorKey );
            case "LAYOUT" -> schemaServiceSupplier.get().deleteLayout( descriptorKey );
            case "PAGE" -> schemaServiceSupplier.get().deletePage( descriptorKey );
            default -> throw new IllegalArgumentException( "unknown component type: " + type );
        };
    }

    @Override
    public void initialize( final BeanContext context )
    {
        schemaServiceSupplier = context.getService( SchemaService.class );
    }
}
