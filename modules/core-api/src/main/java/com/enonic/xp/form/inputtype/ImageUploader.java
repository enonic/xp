package com.enonic.xp.form.inputtype;

import org.apache.commons.lang.StringUtils;

import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.BreaksRequiredContractException;
import com.enonic.xp.form.InvalidTypeException;

final class ImageUploader
    extends InputType
{
    ImageUploader()
    {
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
        throws BreaksRequiredContractException
    {
        boolean isAttachment = ContentPropertyNames.MEDIA_ATTACHMENT.equals( property.getName() );
        boolean isX = ContentPropertyNames.MEDIA_FOCAL_POINT_X.equals( property.getName() );
        boolean isY = ContentPropertyNames.MEDIA_FOCAL_POINT_Y.equals( property.getName() );
        if ( isAttachment && StringUtils.isBlank( property.getString() ) ||
            isX && ( property.getDouble() < 0 || property.getDouble() > 1 ) ||
            isY && ( property.getDouble() < 0 || property.getDouble() > 1 ) )
        {
            throw new BreaksRequiredContractException( property, this );
        }
    }

    @Override
    public void checkTypeValidity( final Property property )
        throws InvalidTypeException
    {
        boolean isAttachment = ContentPropertyNames.MEDIA_ATTACHMENT.equals( property.getName() );
        boolean isX = ContentPropertyNames.MEDIA_FOCAL_POINT_X.equals( property.getName() );
        boolean isY = ContentPropertyNames.MEDIA_FOCAL_POINT_Y.equals( property.getName() );
        if ( isAttachment && !ValueTypes.STRING.equals( property.getType() ) ||
            isX && !ValueTypes.DOUBLE.equals( property.getType() ) ||
            isY && !ValueTypes.DOUBLE.equals( property.getType() ) )
        {
            throw new InvalidTypeException( property, isAttachment ? ValueTypes.STRING : ValueTypes.DOUBLE );
        }
    }

    @Override
    public Value createPropertyValue( final String value, final InputTypeConfig config )
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.DefaultPropertyIdProvider() );
        tree.setString( ContentPropertyNames.MEDIA_ATTACHMENT, value );
        tree.setDouble( PropertyPath.from( ContentPropertyNames.MEDIA_FOCAL_POINT, ContentPropertyNames.MEDIA_FOCAL_POINT_X ), 0.5 );
        tree.setDouble( PropertyPath.from( ContentPropertyNames.MEDIA_FOCAL_POINT, ContentPropertyNames.MEDIA_FOCAL_POINT_Y ), 0.5 );
        return Value.newData( tree.getRoot() );
    }
}
