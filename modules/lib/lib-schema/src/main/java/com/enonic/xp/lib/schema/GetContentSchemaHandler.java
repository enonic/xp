package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.enonic.xp.lib.schema.mapper.SchemaConverter;
import com.enonic.xp.schema.BaseSchema;
import com.enonic.xp.schema.SchemaResult;
import com.enonic.xp.schema.SchemaService;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public class GetContentSchemaHandler
    implements ScriptBean
{
    private String name;

    private String type;

    private Supplier<SchemaService> schemaServiceSupplier;

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
        final SchemaResult<? extends BaseSchema<?>> contentSchema = switch ( type )
        {
            case "CONTENT_TYPE" -> schemaServiceSupplier.get().getContentType( ContentTypeName.from( name ) );
            case "FORM_FRAGMENT" -> schemaServiceSupplier.get().getFormFragment( FormFragmentName.from( name ) );
            case "MIXIN" -> schemaServiceSupplier.get().getMixin( MixinName.from( name ) );
            default -> throw new IllegalArgumentException( "illegal schema type: " + type );
        };

        return contentSchema != null ? SchemaConverter.convert( contentSchema ) : null;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        schemaServiceSupplier = context.getService( SchemaService.class );
    }
}
