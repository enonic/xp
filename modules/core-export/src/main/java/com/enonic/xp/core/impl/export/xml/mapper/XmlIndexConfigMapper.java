package com.enonic.xp.core.impl.export.xml.mapper;

import com.enonic.wem.api.index.IndexConfig;
import com.enonic.xp.core.impl.export.xml.XmlIndexConfig;

class XmlIndexConfigMapper
{

    static XmlIndexConfig toXml( final IndexConfig indexConfig )
    {
        final XmlIndexConfig xmlIndexConfig = new XmlIndexConfig();

        xmlIndexConfig.setDecideByType( indexConfig.isDecideByType() );
        xmlIndexConfig.setEnabled( indexConfig.isEnabled() );
        xmlIndexConfig.setFulltext( indexConfig.isFulltext() );
        xmlIndexConfig.setIncludeInAllText( indexConfig.isIncludeInAllText() );
        xmlIndexConfig.setNGram( indexConfig.isnGram() );

        return xmlIndexConfig;
    }

}
