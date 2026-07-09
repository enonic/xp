package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.lib.schema.mapper.PhrasesMapper;
import com.enonic.xp.schema.CreatePhrasesParams;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.schema.SchemaService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class CreatePhrasesHandler
    implements ScriptBean
{
    private String application;

    private String name;

    private String resource;

    private Supplier<SchemaService> schemaServiceSupplier;

    public void setApplication( final String application )
    {
        this.application = application;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public void setResource( final String resource )
    {
        this.resource = resource;
    }

    public Object execute()
    {
        final CreatePhrasesParams params = CreatePhrasesParams.create()
            .key( ApplicationKey.from( application ) )
            .name( name )
            .resource( resource )
            .build();

        final Resource result = schemaServiceSupplier.get().createPhrases( params );

        return new PhrasesMapper( result );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        schemaServiceSupplier = context.getService( SchemaService.class );
    }
}
