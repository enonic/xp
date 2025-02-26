package com.enonic.xp.lib.node.mapper;

import org.junit.jupiter.api.Test;

import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.testing.helper.JsonAssert;

public class IndexConfigDocMapperTest
{
    @Test
    public void all()
        throws Exception
    {
        final IndexConfigDocument doc = PatternIndexConfigDocument.create().
            defaultConfig( IndexConfig.BY_TYPE ).
            add( "path", IndexConfig.PATH ).
            add( "none", IndexConfig.NONE ).
            add( "minimal", IndexConfig.MINIMAL ).
            add( "full", IndexConfig.FULLTEXT ).
            add( "byType", IndexConfig.BY_TYPE ).
            add( "property1", IndexConfig.BY_TYPE ).
            add( "property1.*", IndexConfig.BY_TYPE ).
            add( "property1.x", IndexConfig.BY_TYPE ).
            add( "property1.property2", IndexConfig.BY_TYPE ).
            add( "property1.*.property3", IndexConfig.create( IndexConfig.BY_TYPE ).addLanguage( "en" ).build() ).
            build();

        JsonAssert.assertMapper( getClass(), "index_config_full.json", new IndexConfigDocMapper( doc ) );
    }
}
