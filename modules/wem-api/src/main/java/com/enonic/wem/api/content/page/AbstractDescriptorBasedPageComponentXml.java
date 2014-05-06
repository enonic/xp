package com.enonic.wem.api.content.page;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.RootDataSetXml;


public abstract class AbstractDescriptorBasedPageComponentXml
    extends AbstractPageComponentXml
{
    @XmlAttribute(name = "descriptor", required = false)
    String descriptor;

    @XmlElement(name = "config", required = true)
    private RootDataSetXml config;

    public void from( final PageComponent component )
    {
        this.name = component.getName().toString();
        if ( component.getDescriptor() != null )
        {
            this.descriptor = component.getDescriptor().toString();
        }
        this.config = new RootDataSetXml();
        this.config.from( component.getConfig() );
    }

    public void to( final AbstractDescriptorBasedPageComponent.Builder builder )
    {
        builder.name( new ComponentName( this.name ) );
        if ( StringUtils.isNotBlank( this.descriptor ) )
        {
            builder.descriptor( toDescriptorKey( this.descriptor ) );
        }
        RootDataSet config = new RootDataSet();
        if ( this.config != null )
        {
            this.config.to( config );
        }
        builder.config( config );
    }

    protected abstract DescriptorKey toDescriptorKey( String s );

}
