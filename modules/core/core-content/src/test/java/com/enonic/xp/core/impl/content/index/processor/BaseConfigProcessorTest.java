package com.enonic.xp.core.impl.content.index.processor;

import java.util.Set;

import org.junit.Test;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PathIndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;

import static org.junit.Assert.*;

import static com.enonic.xp.content.ContentPropertyNames.APPLICATION_KEY;
import static com.enonic.xp.content.ContentPropertyNames.ATTACHMENT;
import static com.enonic.xp.content.ContentPropertyNames.CREATED_TIME;
import static com.enonic.xp.content.ContentPropertyNames.CREATOR;
import static com.enonic.xp.content.ContentPropertyNames.DATA;
import static com.enonic.xp.content.ContentPropertyNames.EXTRA_DATA;
import static com.enonic.xp.content.ContentPropertyNames.MODIFIED_TIME;
import static com.enonic.xp.content.ContentPropertyNames.MODIFIER;
import static com.enonic.xp.content.ContentPropertyNames.OWNER;
import static com.enonic.xp.content.ContentPropertyNames.SITE;
import static com.enonic.xp.content.ContentPropertyNames.SITECONFIG;
import static com.enonic.xp.content.ContentPropertyNames.TYPE;

public class BaseConfigProcessorTest
{
    final BaseConfigProcessor processor = new BaseConfigProcessor();

    @Test
    public void test_default_config()
        throws Exception
    {
        final PatternIndexConfigDocument.Builder builder = PatternIndexConfigDocument.create();

        processor.processDocument( builder );

        assertEquals( IndexConfig.BY_TYPE, builder.build().getDefaultConfig() );
    }

    @Test
    public void test_default_analyzer()
        throws Exception
    {
        final PatternIndexConfigDocument.Builder builder = PatternIndexConfigDocument.create();

        processor.processDocument( builder );

        assertEquals( ContentConstants.DOCUMENT_INDEX_DEFAULT_ANALYZER, builder.build().getAnalyzer() );
    }

    @Test
    public void test_index_configs()
        throws Exception
    {
        final PatternIndexConfigDocument.Builder builder = PatternIndexConfigDocument.create();

        processor.processDocument( builder );

        Set<PathIndexConfig> indexConfigs = builder.build().getPathIndexConfigs();

        assertEquals( 10, indexConfigs.size() );

        assertTrue( indexConfigs.contains(
            PathIndexConfig.create().path( PropertyPath.from( CREATOR ) ).indexConfig( IndexConfig.MINIMAL ).build() ) );
        assertTrue( indexConfigs.contains(
            PathIndexConfig.create().path( PropertyPath.from( MODIFIER ) ).indexConfig( IndexConfig.MINIMAL ).build() ) );
        assertTrue( indexConfigs.contains(
            PathIndexConfig.create().path( PropertyPath.from( CREATED_TIME ) ).indexConfig( IndexConfig.MINIMAL ).build() ) );
        assertTrue( indexConfigs.contains(
            PathIndexConfig.create().path( PropertyPath.from( MODIFIED_TIME ) ).indexConfig( IndexConfig.MINIMAL ).build() ) );
        assertTrue( indexConfigs.contains(
            PathIndexConfig.create().path( PropertyPath.from( OWNER ) ).indexConfig( IndexConfig.MINIMAL ).build() ) );
        assertTrue( indexConfigs.contains(
            PathIndexConfig.create().path( PropertyPath.from( DATA, SITECONFIG, APPLICATION_KEY ) ).indexConfig(
                IndexConfig.MINIMAL ).build() ) );
        assertTrue(
            indexConfigs.contains( PathIndexConfig.create().path( PropertyPath.from( SITE ) ).indexConfig( IndexConfig.NONE ).build() ) );
        assertTrue( indexConfigs.contains(
            PathIndexConfig.create().path( PropertyPath.from( TYPE ) ).indexConfig( IndexConfig.MINIMAL ).build() ) );
        assertTrue( indexConfigs.contains(
            PathIndexConfig.create().path( PropertyPath.from( ATTACHMENT ) ).indexConfig( IndexConfig.MINIMAL ).build() ) );
        assertTrue( indexConfigs.contains(
            PathIndexConfig.create().path( PropertyPath.from( EXTRA_DATA ) ).indexConfig( IndexConfig.MINIMAL ).build() ) );

    }
}
