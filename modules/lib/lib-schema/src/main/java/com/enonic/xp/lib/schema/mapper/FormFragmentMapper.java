package com.enonic.xp.lib.schema.mapper;

import com.enonic.xp.resource.DynamicContentSchemaType;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.schema.formfragment.FormFragmentDescriptor;
import com.enonic.xp.script.serializer.MapGenerator;

public final class FormFragmentMapper
    extends SchemaMapper<FormFragmentDescriptor>
{
    public FormFragmentMapper( final DynamicSchemaResult<FormFragmentDescriptor> schema )
    {
        super( schema );
    }

    public void serialize( final MapGenerator gen )
    {
        super.serialize( gen );
        DynamicSchemaSerializer.serializeForm( gen, descriptor.getForm() );
    }

    @Override
    protected String getType()
    {
        return DynamicContentSchemaType.FORM_FRAGMENT.name();
    }
}
