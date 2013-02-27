package com.enonic.wem.core.index.elastic;

import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import static junit.framework.Assert.assertEquals;


public class IndexSettingsSourceProviderTest
{

    private IndexSettingsSourceProvider indexSettingsSourceProvider = new IndexSettingsSourceProvider();

    @Test
    public void testGetSources()
        throws Exception
    {

        final ResourcePatternResolver resourcePatternResolver = Mockito.mock( ResourcePatternResolver.class );

        Mockito.when( resourcePatternResolver.getResources( IndexSettingsSourceProvider.INDEX_SETTINGS_LOCATION ) ).thenReturn(
            new Resource[]{new ClassPathResource( "com/enonic/wem/core/index/elastic/wem-analyzer-settings.json" ),
                new ClassPathResource( "com/enonic/wem/core/index/elastic/wem-other-settings.json" )} );

        indexSettingsSourceProvider.setResourcePatternResolver( resourcePatternResolver );
        indexSettingsSourceProvider.init();

        final List<String> sources = indexSettingsSourceProvider.getSources();

        assertEquals( 2, sources.size() );


    }
}
