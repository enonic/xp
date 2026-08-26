package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.enonic.xp.lib.schema.mapper.SchemaConverter;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.DynamicSchemaService;
import com.enonic.xp.schema.BaseSchema;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.schema.mixin.MixinName;
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
        final DynamicSchemaService service = dynamicSchemaServiceSupplier.get();

        final DynamicSchemaResult<? extends BaseSchema<?>> result = switch ( type )
        {
            case "CONTENT_TYPE" -> service.getContentType( ContentTypeName.from( name ) );
            case "FORM_FRAGMENT" -> service.getFormFragment( FormFragmentName.from( name ) );
            case "MIXIN" -> service.getMixin( MixinName.from( name ) );
            default -> throw new IllegalArgumentException( "illegal schema type: " + type );
        };

        return result != null ? SchemaConverter.convert( result ) : null;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        dynamicSchemaServiceSupplier = context.getService( DynamicSchemaService.class );
    }
}
