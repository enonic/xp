package com.enonic.xp.inputtype;


import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.util.Reference;

final class VectorUploaderType
    extends InputTypeBase
{
    public final static VectorUploaderType INSTANCE = new VectorUploaderType();

    private VectorUploaderType()
    {
        super( InputTypeName.MEDIA_VECTOR );
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
