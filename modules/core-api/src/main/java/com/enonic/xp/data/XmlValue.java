package com.enonic.xp.data;

final class XmlValue
    extends Value
{
    public XmlValue( final String value )
    {
        super( ValueTypes.XML, value );
    }

    public XmlValue( final XmlValue source )
    {
        super( ValueTypes.XML, source.getObject() );
    }

    @Override
    public Value copy( final PropertyTree tree )
    {
        return new XmlValue( this );
    }
}
