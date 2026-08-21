package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.enonic.xp.lib.schema.mapper.MacroDescriptorMapper;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.schema.CreateMacroParams;
import com.enonic.xp.schema.SchemaResult;
import com.enonic.xp.schema.SchemaService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class CreateMacroHandler
    implements ScriptBean
{
    private String key;

    private String resource;

    private Supplier<SchemaService> schemaServiceSupplier;

    public void setKey( final String key )
    {
        this.key = key;
    }

    public void setResource( final String resource )
    {
        this.resource = resource;
    }

    public MacroDescriptorMapper execute()
    {
        final CreateMacroParams params =
            CreateMacroParams.create().key( MacroKey.from( key ) ).resource( resource ).build();

        final SchemaResult<MacroDescriptor> result = schemaServiceSupplier.get().createMacro( params );

        return new MacroDescriptorMapper( result.getSchema(), result.getResource() );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        schemaServiceSupplier = context.getService( SchemaService.class );
    }
}
