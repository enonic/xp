package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.enonic.xp.resource.DeleteDynamicContentSchemaParams;
import com.enonic.xp.resource.DynamicContentSchemaType;
import com.enonic.xp.resource.DynamicSchemaService;
import com.enonic.xp.schema.BaseSchemaName;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.schema.xdata.MixinName;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public class DeleteDynamicContentSchemaHandler
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

    public boolean execute()
    {
        final DynamicContentSchemaType dynamicContentSchemaType = DynamicContentSchemaType.valueOf( type );
        BaseSchemaName schemaName;
        switch ( dynamicContentSchemaType )
        {
            case FORM_FRAGMENT:
                schemaName = FormFragmentName.from( name );
                break;
            case CONTENT_TYPE:
                schemaName = ContentTypeName.from( name );
                break;
            case MIXIN:
                schemaName = MixinName.from( name );
                break;
            default:
                throw new IllegalArgumentException( "illegal schema type: " + dynamicContentSchemaType );

        }
        final DeleteDynamicContentSchemaParams params =
            DeleteDynamicContentSchemaParams.create().name( schemaName ).type( dynamicContentSchemaType ).build();

        return dynamicSchemaServiceSupplier.get().deleteContentSchema( params );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        dynamicSchemaServiceSupplier = context.getService( DynamicSchemaService.class );
    }
}
