package com.enonic.wem.core.content.datatype;


public class GeographicCoordinate
    extends AbstractDataType
{
    GeographicCoordinate( int key )
    {
        super( key, JavaType.STRING );
    }

    @Override
    public String getIndexableString( final Object value )
    {
        return null;
    }

    @Override
    public void checkValidity( final Object value )
        throws InvalidValueTypeException
    {
        super.checkValidity( value );

        String s = (String) value;
        // TODO: check s is on valid format, example: 40.446195, -79.948862
    }
}
