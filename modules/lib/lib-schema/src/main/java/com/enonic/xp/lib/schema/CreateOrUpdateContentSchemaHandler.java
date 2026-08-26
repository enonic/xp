package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.google.common.io.ByteSource;

import com.enonic.xp.lib.schema.mapper.SchemaConverter;
import com.enonic.xp.resource.CreateOrUpdateContentSchemaParams;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.DynamicSchemaService;
import com.enonic.xp.schema.BaseSchema;
import com.enonic.xp.schema.BaseSchemaName;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class CreateOrUpdateContentSchemaHandler
    implements ScriptBean
{
    private String name;

    private String type;

    private String resource;

    private ByteSource iconData;

    private String iconMimeType;

    private boolean removeIcon;

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

    public void setIconData( final ByteSource iconData )
    {
        this.iconData = iconData;
    }

    public void setIconMimeType( final String iconMimeType )
    {
        this.iconMimeType = iconMimeType;
    }

    public void setRemoveIcon( final boolean removeIcon )
    {
        this.removeIcon = removeIcon;
    }

    public Object execute()
    {
        final DynamicSchemaService service = dynamicSchemaServiceSupplier.get();

        final DynamicSchemaResult<? extends BaseSchema<?>> result = switch ( type )
        {
            case "CONTENT_TYPE" -> service.createOrUpdateContentType( params( ContentTypeName.from( name ) ) );
            case "FORM_FRAGMENT" -> service.createOrUpdateFormFragment( params( FormFragmentName.from( name ) ) );
            case "MIXIN" -> service.createOrUpdateMixin( params( MixinName.from( name ) ) );
            default -> throw new IllegalArgumentException( "illegal schema type: " + type );
        };

        return SchemaConverter.convert( result );
    }

    private CreateOrUpdateContentSchemaParams params( final BaseSchemaName schemaName )
    {
        return CreateOrUpdateContentSchemaParams.create()
            .name( schemaName )
            .resource( resource )
            .icon( IconUpdates.from( iconData, iconMimeType, removeIcon ) )
            .build();
    }

    @Override
    public void initialize( final BeanContext context )
    {
        dynamicSchemaServiceSupplier = context.getService( DynamicSchemaService.class );
    }
}
