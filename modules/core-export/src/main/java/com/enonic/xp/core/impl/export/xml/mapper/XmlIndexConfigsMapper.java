package com.enonic.xp.core.impl.export.xml.mapper;

import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.PathIndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.core.impl.export.xml.XmlIndexConfigs;

class XmlIndexConfigsMapper
{
    static XmlIndexConfigs toXml( final IndexConfigDocument indexConfigDocument )
    {
        if ( indexConfigDocument instanceof PatternIndexConfigDocument )
        {
            return toXml( (PatternIndexConfigDocument) indexConfigDocument );
        }

        return new XmlIndexConfigs();
    }

    private static XmlIndexConfigs toXml( final PatternIndexConfigDocument patternIndexConfigDocument )
    {
        final XmlIndexConfigs xmlIndexConfigs = new XmlIndexConfigs();

        xmlIndexConfigs.setAnalyzer( patternIndexConfigDocument.getAnalyzer() );
        xmlIndexConfigs.setDefaultConfig( XmlIndexConfigMapper.toXml( patternIndexConfigDocument.getDefaultConfig() ) );

        xmlIndexConfigs.setPathIndexConfigs( new XmlIndexConfigs.PathIndexConfigs() );

        for ( final PathIndexConfig pathIndexConfig : patternIndexConfigDocument.getPathIndexConfigs() )
        {
            xmlIndexConfigs.getPathIndexConfigs().getPathIndexConfig().add( XmlPathIndexConfigMapper.toXml( pathIndexConfig ) );
        }

        return xmlIndexConfigs;
    }

}
