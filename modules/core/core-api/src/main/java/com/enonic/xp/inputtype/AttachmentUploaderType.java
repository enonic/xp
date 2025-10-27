package com.enonic.xp.inputtype;


import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.util.GenericValue;

final class AttachmentUploaderType
    extends InputTypeBase
{
    public static final AttachmentUploaderType INSTANCE = new AttachmentUploaderType();

    private AttachmentUploaderType()
    {
        super( InputTypeName.ATTACHMENT_UPLOADER );
    }

    @Override
    public Value createValue( final Value value, final GenericValue config )
    {
        return ValueFactory.newString( value.asString() );
    }

    @Override
    public void validate( final Property property, final GenericValue config )
    {
    }
}
