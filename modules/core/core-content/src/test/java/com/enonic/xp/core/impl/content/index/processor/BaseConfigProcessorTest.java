package com.enonic.xp.core.impl.content.index.processor;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.index.PathIndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.schema.content.ContentTypeService;

import static com.enonic.xp.content.ContentPropertyNames.APPLICATION_KEY;
import static com.enonic.xp.content.ContentPropertyNames.ATTACHMENT;
import static com.enonic.xp.content.ContentPropertyNames.CREATED_TIME;
import static com.enonic.xp.content.ContentPropertyNames.CREATOR;
import static com.enonic.xp.content.ContentPropertyNames.DATA;
import static com.enonic.xp.content.ContentPropertyNames.MODIFIED_TIME;
import static com.enonic.xp.content.ContentPropertyNames.MODIFIER;
import static com.enonic.xp.content.ContentPropertyNames.OWNER;
import static com.enonic.xp.content.ContentPropertyNames.SITE;
import static com.enonic.xp.content.ContentPropertyNames.SITECONFIG;
import static com.enonic.xp.content.ContentPropertyNames.TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BaseConfigProcessorTest
{
    final BaseConfigProcessor processor = new BaseConfigProcessor();

    private ContentTypeService contentTypeService;

    @BeforeEach
    void setUp()
    {
        this.contentTypeService = Mockito.mock( ContentTypeService.class );
    }

    @Test
    void test_default_config()
    {
        assertEquals( IndexConfig.BY_TYPE, processor.processDocument( PatternIndexConfigDocument.empty() ).getDefaultConfig() );
    }

    @Test
    void test_default_analyzer()
    {
        assertEquals( ContentConstants.DOCUMENT_INDEX_DEFAULT_ANALYZER,
                      processor.processDocument( PatternIndexConfigDocument.empty() ).getAnalyzer() );
    }

    @Test
    void test_index_configs()
    {
        Set<PathIndexConfig> indexConfigs = processor.processDocument( PatternIndexConfigDocument.empty() ).getPathIndexConfigs();

        assertEquals( 9, indexConfigs.size() );

        assertTrue( indexConfigs.contains(
            PathIndexConfig.create().path( IndexPath.from( CREATOR ) ).indexConfig( IndexConfig.MINIMAL ).build() ) );
        assertTrue( indexConfigs.contains(
            PathIndexConfig.create().path( IndexPath.from( MODIFIER ) ).indexConfig( IndexConfig.MINIMAL ).build() ) );
        assertTrue( indexConfigs.contains(
            PathIndexConfig.create().path( IndexPath.from( CREATED_TIME ) ).indexConfig( IndexConfig.MINIMAL ).build() ) );
        assertTrue( indexConfigs.contains(
            PathIndexConfig.create().path( IndexPath.from( MODIFIED_TIME ) ).indexConfig( IndexConfig.MINIMAL ).build() ) );
        assertTrue( indexConfigs.contains(
            PathIndexConfig.create().path( IndexPath.from( OWNER ) ).indexConfig( IndexConfig.MINIMAL ).build() ) );
        assertTrue( indexConfigs.contains( PathIndexConfig.create().path( IndexPath.from( DATA, SITECONFIG, APPLICATION_KEY ) ).indexConfig(
                IndexConfig.MINIMAL ).build() ) );
        assertTrue(
            indexConfigs.contains( PathIndexConfig.create().path( IndexPath.from( SITE ) ).indexConfig( IndexConfig.NONE ).build() ) );
        assertTrue( indexConfigs.contains(
            PathIndexConfig.create().path( IndexPath.from( TYPE ) ).indexConfig( IndexConfig.MINIMAL ).build() ) );
        assertTrue( indexConfigs.contains(
            PathIndexConfig.create().path( IndexPath.from( ATTACHMENT ) ).indexConfig( IndexConfig.MINIMAL ).build() ) );

    }

}
