package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.UpdateNamespaceParams;
import com.enonic.xp.lib.schema.mapper.NamespaceMapper;
import com.enonic.xp.schema.SchemaService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class UpdateNamespaceHandler
    implements ScriptBean
{
    private String key;

    private String description;

    private Supplier<SchemaService> schemaServiceSupplier;

    public void setKey( final String key )
    {
        this.key = key;
    }

    public void setDescription( final String description )
    {
        this.description = description;
    }

    public NamespaceMapper execute()
    {
        final UpdateNamespaceParams params =
            UpdateNamespaceParams.create().key( ApplicationKey.from( key ) ).description( description ).build();

        return new NamespaceMapper( schemaServiceSupplier.get().updateNamespace( params ) );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        schemaServiceSupplier = context.getService( SchemaService.class );
    }
}
