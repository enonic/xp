package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.enonic.xp.lib.schema.mapper.MacroDescriptorMapper;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.SchemaService;
import com.enonic.xp.resource.GetDynamicMacroParams;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class GetDynamicMacroHandler
    implements ScriptBean
{
    private String key;

    private Supplier<SchemaService> schemaServiceSupplier;

    public void setKey( final String key )
    {
        this.key = key;
    }

    public MacroDescriptorMapper execute()
    {
        final GetDynamicMacroParams params = GetDynamicMacroParams.create().key( MacroKey.from( key ) ).build();

        final DynamicSchemaResult<MacroDescriptor> result = schemaServiceSupplier.get().getMacro( params );

        return result != null ? new MacroDescriptorMapper( result.getSchema(), result.getResource() ) : null;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        schemaServiceSupplier = context.getService( SchemaService.class );
    }
}
