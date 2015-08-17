package com.enonic.xp.inputtype;

import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.UUIDPropertyIdProvider;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;

final class ImageUploaderType
    extends InputTypeBase
{
    public final static ImageUploaderType INSTANCE = new ImageUploaderType();

    private ImageUploaderType()
    {
        super( InputTypeName.IMAGE_UPLOADER );
    }

    @Override
    public Value createValue( final String value, final InputTypeConfig config )
    {
        PropertyTree tree = new PropertyTree( new UUIDPropertyIdProvider() );
        tree.setString( ContentPropertyNames.MEDIA_ATTACHMENT, value );
        return ValueFactory.newPropertySet( tree.getRoot() );
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
        boolean isAttachment = ContentPropertyNames.MEDIA_ATTACHMENT.equals( property.getName() );
        boolean isX = ContentPropertyNames.MEDIA_FOCAL_POINT_X.equals( property.getName() );
        boolean isY = ContentPropertyNames.MEDIA_FOCAL_POINT_Y.equals( property.getName() );

        if ( isAttachment )
        {
            validateType( property, ValueTypes.STRING );
        }

        if ( isX || isY )
        {
            validateType( property, ValueTypes.DOUBLE );
        }
    }
}
