package com.enonic.wem.api.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.data.type.ValueType;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.xml.XmlObject;

@XmlRootElement(name = "property")
public final class PropertyXml
    implements XmlObject<Property, DataSet>, DataXml
{
    @XmlAttribute(name = "name", required = true)
    private String name;

    @XmlAttribute(name = "type", required = true)
    private String type;

    @XmlElementRef(name = "data", type = RootDataSetValueXml.class)
    @XmlMixed
    private List<Object> values = new ArrayList<>();

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public void from( final Property property )
    {
        this.name = property.getName();
        this.type = property.getValueType().getName();
        if ( property.getValueType().equals( ValueTypes.DATA ) )
        {
            final RootDataSetValueXml rootDataSetValueXml = new RootDataSetValueXml();
            rootDataSetValueXml.from( property.getData() );
            this.values.add( rootDataSetValueXml );
        }
        else
        {
            this.values.add( property.getString() );
        }
    }

    @Override
    public void to( final DataSet output )
    {
        final ValueType valueType = ValueTypes.parseByName( this.type );
        if ( valueType.equals( ValueTypes.DATA ) )
        {
            boolean found = false;
            for ( Object value : this.values )
            {
                if ( value instanceof RootDataSetValueXml )
                {
                    Preconditions.checkArgument( !found, "Unexpected number of RootDataSet" );
                    final RootDataSetValueXml rootDataSetValueXml = (RootDataSetValueXml) value;
                    final RootDataSet valueRootDataSet = new RootDataSet();
                    rootDataSetValueXml.to( valueRootDataSet );
                    final Property property = valueType.newProperty( this.name, valueRootDataSet );
                    output.add( property );
                    found = true;
                }
            }
        }
        else
        {
            final Property property = valueType.newProperty( this.name, this.values.get( 0 ) );
            output.add( property );
        }
    }
}
