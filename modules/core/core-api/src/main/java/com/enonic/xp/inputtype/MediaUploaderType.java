package com.enonic.xp.inputtype;


import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;

final class MediaUploaderType
    extends InputTypeBase
{
    public static final MediaUploaderType INSTANCE = new MediaUploaderType();

    private MediaUploaderType()
    {
        super( InputTypeName.MEDIA_UPLOADER );
    }

    @Override
    public Value createValue( final Value value, final GenericValue config )
    {
        return ValueFactory.newPropertySet( value.asData() );
    }

    @Override
    public void validate( final Property property, final GenericValue config )
    {
        if ( ContentPropertyNames.MEDIA_ATTACHMENT.equals( property.getName() ) )
        {
            validateType( property, ValueTypes.STRING );
        }
    }
}
