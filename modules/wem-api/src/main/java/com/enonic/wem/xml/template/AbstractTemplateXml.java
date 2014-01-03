package com.enonic.wem.xml.template;

import javax.xml.bind.annotation.XmlElement;

import com.enonic.wem.api.content.page.PageDescriptorKey;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.Template;
import com.enonic.wem.api.content.page.image.ImageDescriptorKey;
import com.enonic.wem.api.content.page.image.ImageTemplate;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorKey;
import com.enonic.wem.api.content.page.layout.LayoutTemplate;
import com.enonic.wem.api.content.page.part.PartDescriptorKey;
import com.enonic.wem.api.content.page.part.PartTemplate;
import com.enonic.wem.api.data.DataSetXml;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.xml.XmlObject;

public abstract class AbstractTemplateXml<I, O>
    implements XmlObject<I, O>
{
    @XmlElement(name = "name", required = true)
    private String name;

    @XmlElement(name = "display-name", required = false)
    private String displayName;

    @XmlElement(name = "descriptor", required = false)
    private String descriptor;

    @XmlElement(name = "config", required = false)
    private DataSetXml config = new DataSetXml();


    protected void fromTemplate( final Template template )
    {
        this.name = template.getName().toString();
        this.displayName = template.getDisplayName();
        this.descriptor = template.getDescriptor().toString();
        final RootDataSet cfgDataSet = template.getConfig();
        if ( cfgDataSet != null )
        {
            this.config.from( cfgDataSet );
        }
    }

    protected void toTemplate( final Template.BaseTemplateBuilder builder )
    {
        builder.displayName( this.displayName );
        if ( builder instanceof PartTemplate.Builder )
        {
            builder.descriptor( PartDescriptorKey.from( this.descriptor ) );
        }
        else if ( builder instanceof PageTemplate.Builder )
        {
            builder.descriptor( PageDescriptorKey.from( this.descriptor ) );
        }
        else if ( builder instanceof ImageTemplate.Builder )
        {
            builder.descriptor( ImageDescriptorKey.from( this.descriptor ) );
        }
        else if ( builder instanceof LayoutTemplate.Builder )
        {
            builder.descriptor( LayoutDescriptorKey.from( this.descriptor ) );
        }
        final RootDataSet dataSet = new RootDataSet();
        this.config.to( dataSet );
        builder.config( dataSet );
    }

    public String getName()
    {
        return name;
    }
}
