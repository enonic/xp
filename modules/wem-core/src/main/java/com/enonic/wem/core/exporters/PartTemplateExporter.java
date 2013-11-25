package com.enonic.wem.core.exporters;

import com.enonic.wem.api.content.page.PartTemplate;
import com.enonic.wem.xml.XmlSerializers;
import com.enonic.wem.xml.template.PartTemplateXml;

@XMLFilename("PartTemplate.xml")
public final class PartTemplateExporter
    extends EntityExporter<PartTemplate>
{
    protected String serializeToXMLString( final PartTemplate partTemplate )
    {
        final PartTemplateXml partTemplateXml = new PartTemplateXml();
        partTemplateXml.from( partTemplate );
        return XmlSerializers.partTemplate().serialize( partTemplateXml );
    }
}
