package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.enonic.xp.lib.schema.mapper.MacroDescriptorMapper;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.schema.SchemaResult;
import com.enonic.xp.schema.SchemaService;
import com.enonic.xp.schema.GetMacroParams;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class GetMacroHandler
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
        final GetMacroParams params = GetMacroParams.create().key( MacroKey.from( key ) ).build();

        final SchemaResult<MacroDescriptor> result = schemaServiceSupplier.get().getMacro( params );

        return result != null ? new MacroDescriptorMapper( result.getSchema(), result.getResource() ) : null;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        schemaServiceSupplier = context.getService( SchemaService.class );
    }
}
