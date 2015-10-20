package com.enonic.xp.inputtype;


import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;

final class AttachmentType
    extends InputTypeBase
{
    public final static AttachmentType INSTANCE = new AttachmentType();

    private AttachmentType()
    {
        super( InputTypeName.ATTACHMENT );
    }

    @Override
    public Value createValue( final String value, final InputTypeConfig config )
    {
        return ValueFactory.newString( value );
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
    }
}
