package com.enonic.xp.inputtype;


import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;

final class AttachmentUploaderType
    extends InputTypeBase
{
    public final static AttachmentUploaderType INSTANCE = new AttachmentUploaderType();

    private AttachmentUploaderType()
    {
        super( InputTypeName.ATTACHMENT_UPLOADER );
    }

    @Override
    public Value createValue( final Value value, final InputTypeConfig config )
    {
        return ValueFactory.newString( value.asString() );
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
    }
}
