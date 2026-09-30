package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.lib.schema.mapper.PhrasesMapper;
import com.enonic.xp.resource.CreateDynamicPhrasesParams;
import com.enonic.xp.resource.DynamicSchemaService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class CreateDynamicPhrasesHandler
    implements ScriptBean
{
    private String application;

    private String name;

    private String resource;

    private Supplier<DynamicSchemaService> dynamicSchemaServiceSupplier;

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

    public PhrasesMapper execute()
    {
        final CreateDynamicPhrasesParams params =
            CreateDynamicPhrasesParams.create().key( ApplicationKey.from( application ) ).name( name ).resource( resource ).build();
        return new PhrasesMapper( dynamicSchemaServiceSupplier.get().createPhrases( params ) );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        dynamicSchemaServiceSupplier = context.getService( DynamicSchemaService.class );
    }
}
