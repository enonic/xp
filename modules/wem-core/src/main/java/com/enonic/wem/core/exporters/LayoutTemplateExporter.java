package com.enonic.wem.core.exporters;

import com.enonic.wem.api.content.page.LayoutTemplate;
import com.enonic.wem.xml.XmlSerializers;
import com.enonic.wem.xml.template.LayoutTemplateXml;

@XMLFilename("LayoutTemplate.xml")
public final class LayoutTemplateExporter
    extends EntityExporter<LayoutTemplate>
{
    protected String serializeToXMLString( final LayoutTemplate layoutTemplate )
    {
        final LayoutTemplateXml layoutTemplateXml = new LayoutTemplateXml();
        layoutTemplateXml.from( layoutTemplate );
        return XmlSerializers.layoutTemplate().serialize( layoutTemplateXml );
    }
}
