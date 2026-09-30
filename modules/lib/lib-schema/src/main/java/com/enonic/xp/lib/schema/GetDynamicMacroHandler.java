package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.enonic.xp.lib.schema.mapper.MacroDescriptorMapper;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.DynamicSchemaService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class GetDynamicMacroHandler
    implements ScriptBean
{
    private String key;

    private Supplier<DynamicSchemaService> dynamicSchemaServiceSupplier;

    public void setKey( final String key )
    {
        this.key = key;
    }

    public MacroDescriptorMapper execute()
    {
        final DynamicSchemaResult<MacroDescriptor> result = dynamicSchemaServiceSupplier.get().getMacro( MacroKey.from( key ) );
        return result != null ? new MacroDescriptorMapper( result ) : null;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        dynamicSchemaServiceSupplier = context.getService( DynamicSchemaService.class );
    }
}
