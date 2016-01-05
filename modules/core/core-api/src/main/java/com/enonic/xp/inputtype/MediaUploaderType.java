package com.enonic.xp.inputtype;


import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.util.Reference;

final class MediaUploaderType
    extends InputTypeBase
{
    public final static MediaUploaderType INSTANCE = new MediaUploaderType();

    private MediaUploaderType()
    {
        super( InputTypeName.MEDIA_UPLOADER );
    }

    @Override
    public Value createValue( final String value, final InputTypeConfig config )
    {
        return ValueFactory.newReference( Reference.from( value ) );
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
    }
}
