package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.resource.CreateDynamicSchemaParams;
import com.enonic.xp.resource.DynamicSchemaService;
import com.enonic.xp.resource.DynamicSchemaType;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public class CreateDynamicSchemaHandler
    implements ScriptBean
{
    private String key;

    private String type;

    private String resource;

    private Supplier<DynamicSchemaService> dynamicSchemaServiceSupplier;

    public void setKey( final String key )
    {
        this.key = key;
    }

    public void setType( final String type )
    {
        this.type = type;
    }

    public void setResource( final String resource )
    {
        this.resource = resource;
    }

    public String execute()
    {
        final CreateDynamicSchemaParams params = CreateDynamicSchemaParams.create()
            .descriptorKey( DescriptorKey.from( key ) )
            .type( DynamicSchemaType.valueOf( type ) )
            .resource( resource )
            .build();

        return dynamicSchemaServiceSupplier.get().create( params ).readString();
    }

    @Override
    public void initialize( final BeanContext context )
    {
        dynamicSchemaServiceSupplier = context.getService( DynamicSchemaService.class );
    }
}
