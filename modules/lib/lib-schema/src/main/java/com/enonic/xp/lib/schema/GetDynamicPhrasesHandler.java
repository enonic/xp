package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.lib.schema.mapper.PhrasesMapper;
import com.enonic.xp.resource.GetDynamicPhrasesParams;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.SchemaService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class GetDynamicPhrasesHandler
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

    public PhrasesMapper execute()
    {
        final GetDynamicPhrasesParams params =
            GetDynamicPhrasesParams.create().key( ApplicationKey.from( application ) ).name( name ).build();

        final Resource result = schemaServiceSupplier.get().getPhrases( params );

        return result != null ? new PhrasesMapper( result ) : null;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        schemaServiceSupplier = context.getService( SchemaService.class );
    }
}
