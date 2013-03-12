package com.enonic.wem.api.content.data;


import com.enonic.wem.api.content.data.type.DataTypes;

public final class Xml
    extends Data
{
    public Xml( final String name, final String value )
    {
        super( newXml().name( name ).value( value ) );
    }

    public Xml( final XmlBuilder builder )
    {
        super( builder );
    }

    public static XmlBuilder newXml()
    {
        return new XmlBuilder();
    }

    public static class XmlBuilder
        extends BaseBuilder<XmlBuilder>
    {
        public XmlBuilder()
        {
            setType( DataTypes.XML );
        }

        public XmlBuilder value( final String value )
        {
            setValue( value );
            return this;
        }

        @Override
        public Data build()
        {
            return new Xml( this );
        }
    }
}
