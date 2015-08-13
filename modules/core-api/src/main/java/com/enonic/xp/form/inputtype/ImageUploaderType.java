package com.enonic.xp.form.inputtype;

import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.InvalidTypeException;

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
        PropertyTree tree = new PropertyTree( new PropertyTree.DefaultPropertyIdProvider() );
        tree.setString( ContentPropertyNames.MEDIA_ATTACHMENT, value );
        return Value.newData( tree.getRoot() );
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
        boolean isAttachment = ContentPropertyNames.MEDIA_ATTACHMENT.equals( property.getName() );
        boolean isX = ContentPropertyNames.MEDIA_FOCAL_POINT_X.equals( property.getName() );
        boolean isY = ContentPropertyNames.MEDIA_FOCAL_POINT_Y.equals( property.getName() );
        if ( isAttachment && ( ValueTypes.STRING != property.getType() ) ||
            isX && ( ValueTypes.DOUBLE != property.getType() ) ||
            isY && ( ValueTypes.DOUBLE != property.getType() ) )
        {
            throw new InvalidTypeException( property, isAttachment ? ValueTypes.STRING : ValueTypes.DOUBLE );
        }
    }
}
