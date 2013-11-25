package com.enonic.wem.core.exporters;

import com.enonic.wem.api.content.page.ImageTemplate;
import com.enonic.wem.xml.XmlSerializers;
import com.enonic.wem.xml.template.ImageTemplateXml;

@XMLFilename("ImageTemplate.xml")
public final class ImageTemplateExporter
    extends EntityExporter<ImageTemplate>
{
    protected String serializeToXMLString( final ImageTemplate imageTemplate )
    {
        final ImageTemplateXml imageTemplateXml = new ImageTemplateXml();
        imageTemplateXml.from( imageTemplate );
        return XmlSerializers.create( ImageTemplateXml.class ).serialize( imageTemplateXml );
    }
}


