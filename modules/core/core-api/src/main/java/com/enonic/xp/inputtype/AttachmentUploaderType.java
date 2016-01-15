package com.enonic.xp.inputtype;


import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.util.Reference;

final class AttachmentUploaderType
    extends InputTypeBase
{
    public final static AttachmentUploaderType INSTANCE = new AttachmentUploaderType();

    private AttachmentUploaderType()
    {
        super( InputTypeName.ATTACHMENT_UPLOADER );
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
