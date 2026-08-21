package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.google.common.io.ByteSource;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.lib.schema.mapper.IconMapper;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.schema.SchemaService;
import com.enonic.xp.schema.SetComponentIconParams;
import com.enonic.xp.schema.SetMacroIconParams;
import com.enonic.xp.schema.SetSchemaIconParams;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class SetIconHandler
    implements ScriptBean
{
    private String name;

    private String type;

    private ByteSource data;

    private String mimeType;

    private Supplier<SchemaService> schemaServiceSupplier;

    public void setName( final String name )
    {
        this.name = name;
    }

    public void setType( final String type )
    {
        this.type = type;
    }

    public void setData( final ByteSource data )
    {
        this.data = data;
    }

    public void setMimeType( final String mimeType )
    {
        this.mimeType = mimeType;
    }

    public Object execute()
    {
        final Icon icon = switch ( type )
        {
            case "CONTENT_TYPE" -> schemaServiceSupplier.get()
                .setContentTypeIcon(
                    SetSchemaIconParams.create().name( ContentTypeName.from( name ) ).data( data ).mimeType( mimeType ).build() );
            case "FORM_FRAGMENT" -> schemaServiceSupplier.get()
                .setFormFragmentIcon(
                    SetSchemaIconParams.create().name( FormFragmentName.from( name ) ).data( data ).mimeType( mimeType ).build() );
            case "MIXIN" -> schemaServiceSupplier.get()
                .setMixinIcon( SetSchemaIconParams.create().name( MixinName.from( name ) ).data( data ).mimeType( mimeType ).build() );
            case "PART" -> schemaServiceSupplier.get()
                .setPartIcon(
                    SetComponentIconParams.create().descriptorKey( DescriptorKey.from( name ) ).data( data ).mimeType( mimeType ).build() );
            case "MACRO" -> schemaServiceSupplier.get()
                .setMacroIcon( SetMacroIconParams.create().key( MacroKey.from( name ) ).data( data ).mimeType( mimeType ).build() );
            default -> throw new IllegalArgumentException( "icons are not supported for type: " + type );
        };

        return new IconMapper( icon );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        schemaServiceSupplier = context.getService( SchemaService.class );
    }
}