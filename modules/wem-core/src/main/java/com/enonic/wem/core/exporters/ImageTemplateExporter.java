package com.enonic.wem.core.exporters;

import java.io.IOException;
import java.nio.file.Path;

import com.enonic.wem.api.content.page.image.ImageTemplate;
import com.enonic.wem.xml.XmlSerializers;
import com.enonic.wem.xml.template.ImageTemplateXml;

@XMLFilename("ImageTemplate.xml")
public final class ImageTemplateExporter
    extends AbstractEntityExporter<ImageTemplate>
{
    @Override
    protected String toXMLString( final ImageTemplate imageTemplate )
    {
        final ImageTemplateXml imageTemplateXml = new ImageTemplateXml();
        imageTemplateXml.from( imageTemplate );
        return XmlSerializers.create( ImageTemplateXml.class ).serialize( imageTemplateXml );
    }

    @Override
    protected ImageTemplate fromXMLString( final String xml, final Path directoryPath )
        throws IOException
    {
        final ImageTemplate.Builder builder = ImageTemplate.newImageTemplate();
        XmlSerializers.imageTemplate().parse( xml ).to( builder );
        return builder.build();
    }
}


