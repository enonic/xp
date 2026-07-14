package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.resource.DeleteDynamicMacroParams;
import com.enonic.xp.resource.DynamicSchemaService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class DeleteDynamicMacroHandler
    implements ScriptBean
{
    private String key;

    private Supplier<DynamicSchemaService> dynamicSchemaServiceSupplier;

    public void setKey( final String key )
    {
        this.key = key;
    }

    public boolean execute()
    {
        final DeleteDynamicMacroParams params = DeleteDynamicMacroParams.create().key( MacroKey.from( key ) ).build();

        return dynamicSchemaServiceSupplier.get().deleteMacro( params );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        dynamicSchemaServiceSupplier = context.getService( DynamicSchemaService.class );
    }
}
