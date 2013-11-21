package com.enonic.wem.xml.template;

import javax.xml.bind.annotation.XmlElement;

import com.enonic.wem.api.content.page.Template;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.xml.XmlObject;
import com.enonic.wem.xml.data.DataSetXml;

public abstract class AbstractTemplateXml<I, O>
    implements XmlObject<I, O>
{
    @XmlElement(name = "display-name", required = false)
    private String displayName;

    @XmlElement(name = "descriptor", required = false)
    private String descriptor;

    @XmlElement(name = "config", required = false)
    private DataSetXml config = new DataSetXml();


    protected void fromTemplate( final Template template )
    {
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
        builder.descriptor( ModuleResourceKey.from( this.descriptor ) );
        final RootDataSet dataSet = new RootDataSet();
        this.config.to( dataSet );
        builder.config( dataSet );
    }

}
