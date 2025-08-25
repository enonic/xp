package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.enonic.xp.lib.schema.mapper.SchemaConverter;
import com.enonic.xp.resource.DynamicContentSchemaType;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.DynamicSchemaService;
import com.enonic.xp.resource.GetDynamicContentSchemaParams;
import com.enonic.xp.schema.BaseSchema;
import com.enonic.xp.schema.BaseSchemaName;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public class GetDynamicContentSchemaHandler
    implements ScriptBean
{
    private String name;

    private String type;

    private Supplier<DynamicSchemaService> dynamicSchemaServiceSupplier;

    public void setName( final String name )
    {
        this.name = name;
    }

    public void setType( final String type )
    {
        this.type = type;
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

        final GetDynamicContentSchemaParams params =
            GetDynamicContentSchemaParams.create().name( schemaName ).type( dynamicContentSchemaType ).build();

        final DynamicSchemaResult<BaseSchema<?>> contentSchema = dynamicSchemaServiceSupplier.get().getContentSchema( params );
        return contentSchema != null ? SchemaConverter.convert( contentSchema ) : null;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        dynamicSchemaServiceSupplier = context.getService( DynamicSchemaService.class );
    }
}
