package com.enonic.xp.core.impl.export.builder;

import com.google.common.base.Strings;

import com.enonic.wem.api.data.PropertyPath;
import com.enonic.wem.api.index.IndexConfigDocument;
import com.enonic.wem.api.index.PathIndexConfig;
import com.enonic.wem.api.index.PatternIndexConfigDocument;
import com.enonic.xp.core.impl.export.xml.XmlIndexConfigs;
import com.enonic.xp.core.impl.export.xml.XmlPathIndexConfig;

class IndexConfigDocumentXmlBuilder
{
    static IndexConfigDocument build( final XmlIndexConfigs xmlIndexConfigs )
    {
        if ( xmlIndexConfigs == null )
        {
            return PatternIndexConfigDocument.create().build();
        }

        final PatternIndexConfigDocument.Builder builder = PatternIndexConfigDocument.create().
            defaultConfig( IndexConfigXmlBuilder.build( xmlIndexConfigs.getDefaultConfig() ) );

        if ( !Strings.isNullOrEmpty( xmlIndexConfigs.getAnalyzer() ) )
        {
            builder.analyzer( xmlIndexConfigs.getAnalyzer() );
        }

        for ( final XmlPathIndexConfig xmlPathIndexConfig : xmlIndexConfigs.getPathIndexConfigs().getPathIndexConfig() )
        {
            builder.addPattern( PathIndexConfig.create().
                path( PropertyPath.from( xmlPathIndexConfig.getPath() ) ).
                indexConfig( IndexConfigXmlBuilder.build( xmlPathIndexConfig.getIndexConfig() ) ).
                build() );
        }

        return builder.build();
    }
}
