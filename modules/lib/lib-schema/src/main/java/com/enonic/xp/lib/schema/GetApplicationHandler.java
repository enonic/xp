package com.enonic.xp.lib.schema;

import java.util.Optional;
import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.lib.schema.mapper.ApplicationMapper;
import com.enonic.xp.resource.SchemaService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class GetApplicationHandler
    implements ScriptBean
{
    private String key;

    private Supplier<SchemaService> schemaServiceSupplier;


    public ApplicationMapper execute()
    {
        return Optional.ofNullable( schemaServiceSupplier.get().get( ApplicationKey.from( key ) ) )
            .map( ApplicationMapper::new )
            .orElse( null );
    }

    public void setKey( final String key )
    {
        this.key = key;
    }


    @Override
    public void initialize( final BeanContext context )
    {
        schemaServiceSupplier = context.getService( SchemaService.class );
    }
}
