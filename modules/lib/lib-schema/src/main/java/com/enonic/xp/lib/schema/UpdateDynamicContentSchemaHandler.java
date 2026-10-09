package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.enonic.xp.lib.schema.mapper.SchemaConverter;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.DynamicSchemaService;
import com.enonic.xp.resource.UpdateDynamicContentSchemaParams;
import com.enonic.xp.schema.BaseSchema;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class UpdateDynamicContentSchemaHandler
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
        final DynamicSchemaService service = dynamicSchemaServiceSupplier.get();

        final DynamicSchemaResult<? extends BaseSchema<?>> result = switch ( type )
        {
            case "CONTENT_TYPE" -> service.updateContentType(
                UpdateDynamicContentSchemaParams.create().name( ContentTypeName.from( name ) ).resource( resource ).build() );
            case "FORM_FRAGMENT" -> service.updateFormFragment(
                UpdateDynamicContentSchemaParams.create().name( FormFragmentName.from( name ) ).resource( resource ).build() );
            case "MIXIN" -> service.updateMixin(
                UpdateDynamicContentSchemaParams.create().name( MixinName.from( name ) ).resource( resource ).build() );
            default -> throw new IllegalArgumentException( "illegal schema type: " + type );
        };

        return SchemaConverter.convert( result );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        dynamicSchemaServiceSupplier = context.getService( DynamicSchemaService.class );
    }
}
