package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.resource.DynamicSchemaService;
import com.enonic.xp.resource.DynamicSchemaType;
import com.enonic.xp.resource.GetDynamicSchemaParams;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public class GetDynamicSchemaHandler
    implements ScriptBean
{
    private String key;

    private String type;

    private Supplier<DynamicSchemaService> dynamicSchemaServiceSupplier;

    public void setKey( final String key )
    {
        this.key = key;
    }

    public void setType( final String type )
    {
        this.type = type;
    }

    public Resource execute()
    {
        final GetDynamicSchemaParams params =
            GetDynamicSchemaParams.create().descriptorKey( DescriptorKey.from( key ) ).type( DynamicSchemaType.valueOf( type ) ).build();

        return dynamicSchemaServiceSupplier.get().get( params );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        dynamicSchemaServiceSupplier = context.getService( DynamicSchemaService.class );
    }
}
