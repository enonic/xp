package com.enonic.xp.form.inputtype;

import org.apache.commons.lang.StringUtils;

import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.BreaksRequiredContractException;
import com.enonic.xp.form.InvalidTypeException;

final class ImageUploader
    extends InputType
{
    public ImageUploader()
    {
        super( "ImageUploader", null, false );
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
    {
        boolean isAttachment = ContentPropertyNames.MEDIA_ATTACHMENT.equals( property.getName() );
        boolean isX = ContentPropertyNames.MEDIA_FOCAL_POINT_X.equals( property.getName() );
        boolean isY = ContentPropertyNames.MEDIA_FOCAL_POINT_Y.equals( property.getName() );
        if ( isAttachment && StringUtils.isBlank( property.getString() ) ||
            isX && ( property.getDouble() == null || property.getDouble() < 0 || property.getDouble() > 1 ) ||
            isY && ( property.getDouble() == null || property.getDouble() < 0 || property.getDouble() > 1 ) )
        {
            throw new BreaksRequiredContractException( property, this );
        }
    }

    @Override
    public void checkTypeValidity( final Property property )
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

    @Override
    public Value createPropertyValue( final String value, final InputTypeConfig config )
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.DefaultPropertyIdProvider() );
        tree.setString( ContentPropertyNames.MEDIA_ATTACHMENT, value );
        return Value.newData( tree.getRoot() );
    }
}
