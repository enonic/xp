package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.enonic.xp.lib.schema.mapper.SchemaConverter;
import com.enonic.xp.schema.BaseSchema;
import com.enonic.xp.schema.SchemaResult;
import com.enonic.xp.schema.SchemaService;
import com.enonic.xp.schema.UpdateContentSchemaParams;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class UpdateContentSchemaHandler
    implements ScriptBean
{
    private String name;

    private String type;

    private String resource;

    private Supplier<SchemaService> schemaServiceSupplier;

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
        final SchemaResult<? extends BaseSchema<?>> result = switch ( type )
        {
            case "CONTENT_TYPE" -> schemaServiceSupplier.get()
                .updateContentType(
                    UpdateContentSchemaParams.create().name( ContentTypeName.from( name ) ).resource( resource ).build() );
            case "FORM_FRAGMENT" -> schemaServiceSupplier.get()
                .updateFormFragment(
                    UpdateContentSchemaParams.create().name( FormFragmentName.from( name ) ).resource( resource ).build() );
            case "MIXIN" -> schemaServiceSupplier.get()
                .updateMixin( UpdateContentSchemaParams.create().name( MixinName.from( name ) ).resource( resource ).build() );
            default -> throw new IllegalArgumentException( "illegal schema type: " + type );
        };

        return SchemaConverter.convert( result );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        schemaServiceSupplier = context.getService( SchemaService.class );
    }
}
