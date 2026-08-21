package com.enonic.xp.lib.schema;

import java.util.Optional;
import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.lib.schema.mapper.NamespaceMapper;
import com.enonic.xp.schema.SchemaService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class GetNamespaceHandler
    implements ScriptBean
{
    private String key;

    private Supplier<SchemaService> schemaServiceSupplier;

    public void setKey( final String key )
    {
        this.key = key;
    }

    public NamespaceMapper execute()
    {
        return Optional.ofNullable( schemaServiceSupplier.get().getNamespace( ApplicationKey.from( key ) ) )
            .map( NamespaceMapper::new )
            .orElse( null );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        schemaServiceSupplier = context.getService( SchemaService.class );
    }
}
