package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.DeleteDynamicPhrasesParams;
import com.enonic.xp.resource.SchemaService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class DeleteDynamicPhrasesHandler
    implements ScriptBean
{
    private String application;

    private String name;

    private Supplier<SchemaService> schemaServiceSupplier;

    public void setApplication( final String application )
    {
        this.application = application;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public boolean execute()
    {
        final DeleteDynamicPhrasesParams params =
            DeleteDynamicPhrasesParams.create().key( ApplicationKey.from( application ) ).name( name ).build();

        return schemaServiceSupplier.get().deletePhrases( params );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        schemaServiceSupplier = context.getService( SchemaService.class );
    }
}
