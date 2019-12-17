package com.enonic.xp.extractor.impl.config;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

@Component(immediate = true, configurationPid = "com.enonic.xp.extractor")
public class ExtractorConfigImpl
    implements ExtractorConfig
{
    private int bodySizeLimit;

    @Override
    public int getBodySizeLimit()
    {
        return bodySizeLimit;
    }

    @Activate
    public void configure( final Map<String, String> config )
    {
        final ExtractorConfigMap configMap = new ExtractorConfigMap( config );
        this.bodySizeLimit = configMap.getBodySizeLimit();
    }
}
