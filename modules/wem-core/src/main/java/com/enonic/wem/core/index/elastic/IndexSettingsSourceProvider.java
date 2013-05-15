package com.enonic.wem.core.index.elastic;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.springframework.stereotype.Component;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;

import com.enonic.wem.core.index.IndexException;

@Component
public class IndexSettingsSourceProvider
{
    private final static String PREFIX = "META-INF/index/settings/";

    private final static String[] SETTING_FILES = { //
        PREFIX + "wem-analyzer-settings.json" //
    };

    public List<String> getSources()
    {
        final List<String> settings = Lists.newArrayList();
        for ( final String settingFile : SETTING_FILES )
        {
            final String settingsFileContent;
            try
            {
                final URL url = Resources.getResource( settingFile );
                settingsFileContent = Resources.toString( url, Charsets.UTF_8 );
            }
            catch ( IOException e )
            {
                throw new IndexException( "Failed to load settings from file: " + settingFile, e );
            }

            if ( !Strings.isNullOrEmpty( settingsFileContent ) )
            {
                settings.add( settingsFileContent );
            }

        }

        return settings;
    }
}
