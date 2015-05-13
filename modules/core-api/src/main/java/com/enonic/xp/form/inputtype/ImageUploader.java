package com.enonic.xp.form.inputtype;

import org.apache.commons.lang.StringUtils;

import com.enonic.xp.data.Property;
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
        if ( property.getType().equals( ValueTypes.PROPERTY_SET ) )
        {
            // TODO
        }
        else if ( StringUtils.isBlank( property.getString() ) )
        {
            throw new BreaksRequiredContractException( property, this );
        }
    }

    @Override
    public void checkTypeValidity( final Property property )
        throws InvalidTypeException
    {
        // accept STRING for backwards compatibility
        // commented out due to issues with InputValidator
//        if ( !( ValueTypes.PROPERTY_SET.equals( property.getType() ) || ValueTypes.STRING.equals( property.getType() ) ) )
//        {
//            throw new InvalidTypeException( property, ValueTypes.PROPERTY_SET );
//        }
    }

    @Override
    public Value createPropertyValue( final String value, final InputTypeConfig config )
    {
        PropertyTree tree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );
        return Value.newData( tree.newSet() );
    }
}
