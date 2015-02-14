package com.enonic.xp.core.impl.export.xml.mapper;

import com.enonic.xp.index.PathIndexConfig;
import com.enonic.xp.core.impl.export.xml.XmlPathIndexConfig;

class XmlPathIndexConfigMapper
{
    static XmlPathIndexConfig toXml( final PathIndexConfig pathIndexConfig )
    {
        final XmlPathIndexConfig xmlPathIndexConfig = new XmlPathIndexConfig();

        xmlPathIndexConfig.setPath( pathIndexConfig.getPath().toString() );
        xmlPathIndexConfig.setIndexConfig( XmlIndexConfigMapper.toXml( pathIndexConfig.getIndexConfig() ) );

        return xmlPathIndexConfig;
    }
}
