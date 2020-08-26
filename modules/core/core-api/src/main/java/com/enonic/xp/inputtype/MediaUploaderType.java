package com.enonic.xp.inputtype;


import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;

final class MediaUploaderType
    extends InputTypeBase
{
    public static final MediaUploaderType INSTANCE = new MediaUploaderType();

    private MediaUploaderType()
    {
        super( InputTypeName.MEDIA_UPLOADER );
    }

    @Override
    public Value createValue( final Value value, final InputTypeConfig config )
    {
        return ValueFactory.newReference( value.asReference() );
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
    }
}
