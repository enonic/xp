package com.enonic.xp.lib.schema;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.lib.schema.mapper.MacroDescriptorMapper;
import com.enonic.xp.resource.SchemaService;
import com.enonic.xp.resource.ListDynamicMacrosParams;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class ListDynamicMacrosHandler
    implements ScriptBean
{
    private String application;

    private Supplier<SchemaService> schemaServiceSupplier;

    public void setApplication( final String application )
    {
        this.application = application;
    }

    public List<MacroDescriptorMapper> execute()
    {
        final ListDynamicMacrosParams params =
            ListDynamicMacrosParams.create().applicationKey( ApplicationKey.from( application ) ).build();

        return schemaServiceSupplier.get()
            .listMacros( params )
            .stream()
            .map( result -> new MacroDescriptorMapper( result.getSchema(), result.getResource() ) )
            .collect( Collectors.toList() );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        schemaServiceSupplier = context.getService( SchemaService.class );
    }
}
