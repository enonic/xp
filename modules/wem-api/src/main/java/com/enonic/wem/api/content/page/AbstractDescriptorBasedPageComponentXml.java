package com.enonic.wem.api.content.page;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.data.DataSetXml;
import com.enonic.wem.api.data.DataSetXmlAdapter;
import com.enonic.wem.api.data.RootDataSet;


public abstract class AbstractDescriptorBasedPageComponentXml
    extends AbstractPageComponentXml
{
    @XmlAttribute(name = "descriptor", required = false)
    String descriptor;

    @XmlElement(name = "config", required = true)
    @XmlJavaTypeAdapter(DataSetXmlAdapter.class)
    private DataSetXml config = new DataSetXml();

    public void from( final DescriptorBasedPageComponent component )
    {
        this.name = component.getName().toString();
        if ( component.getDescriptor() != null )
        {
            this.descriptor = component.getDescriptor().toString();
        }
        this.config = new DataSetXml();
        this.config.from( component.getConfig() );
    }

    public void to( final AbstractDescriptorBasedPageComponent.Builder builder )
    {
        builder.name( new ComponentName( this.name ) );
        if ( StringUtils.isNotBlank( this.descriptor ) )
        {
            builder.descriptor( toDescriptorKey( this.descriptor ) );
        }
        final RootDataSet config = new RootDataSet();
        if ( this.config != null )
        {
            this.config.to( config );
        }
        builder.config( config );
    }

    protected abstract DescriptorKey toDescriptorKey( String s );

}
