package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.enonic.xp.lib.schema.mapper.SchemaConverter;
import com.enonic.xp.resource.CreateDynamicContentSchemaParams;
import com.enonic.xp.resource.DynamicContentSchemaType;
import com.enonic.xp.resource.DynamicSchemaService;
import com.enonic.xp.schema.BaseSchemaName;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public class CreateDynamicContentSchemaHandler
    implements ScriptBean
{
    private String name;

    private String type;

    private String resource;

    private Supplier<DynamicSchemaService> dynamicSchemaServiceSupplier;

    public void setName( final String name )
    {
        this.name = name;
    }

    public void setType( final String type )
    {
        this.type = type;
    }

    public void setResource( final String resource )
    {
        this.resource = resource;
    }

    public Object execute()
    {
        final DynamicContentSchemaType dynamicContentSchemaType = DynamicContentSchemaType.valueOf( type );
        BaseSchemaName schemaName;
        switch ( dynamicContentSchemaType )
        {
            case MIXIN:
                schemaName = MixinName.from( name );
                break;
            case CONTENT_TYPE:
                schemaName = ContentTypeName.from( name, "yml" );
                break;
            case XDATA:
                schemaName = XDataName.from( name );
                break;
            default:
                throw new IllegalArgumentException( "illegal schema type: " + dynamicContentSchemaType );

        }
        final CreateDynamicContentSchemaParams params =
            CreateDynamicContentSchemaParams.create().name( schemaName ).type( dynamicContentSchemaType ).resource( resource ).build();

        return SchemaConverter.convert( dynamicSchemaServiceSupplier.get().createContentSchema( params ) );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        dynamicSchemaServiceSupplier = context.getService( DynamicSchemaService.class );
    }
}
