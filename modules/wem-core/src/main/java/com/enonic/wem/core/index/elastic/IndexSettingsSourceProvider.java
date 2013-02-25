package com.enonic.wem.core.index.elastic;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;

import com.enonic.wem.core.index.IndexException;

@Component
public class IndexSettingsSourceProvider
{
    public static final String INDEX_SETTINGS_LOCATION =
        ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "/META-INF/index/settings/*-settings.json";

    private ResourcePatternResolver resourcePatternResolver;

    private List<Resource> resources;

    @PostConstruct
    public void init()
        throws Exception
    {
        this.resources = Lists.newArrayList( resourcePatternResolver.getResources( INDEX_SETTINGS_LOCATION ) );
    }

    public List<String> getSources()
    {
        List<String> settings = Lists.newArrayList();

        for ( Resource resource : resources )
        {
            final String settingsFileContent;
            try
            {
                settingsFileContent = CharStreams.toString( new InputStreamReader( resource.getInputStream(), Charsets.UTF_8 ) );
            }
            catch ( IOException e )
            {
                throw new IndexException( "Failed to load settings from file: " + resource.getFilename(), e );
            }

            if ( !Strings.isNullOrEmpty( settingsFileContent ) )
            {
                settings.add( settingsFileContent );
            }

        }

        return settings;
    }

    @Inject
    public void setResourcePatternResolver( final ResourcePatternResolver resourcePatternResolver )
    {
        this.resourcePatternResolver = resourcePatternResolver;
    }
}
