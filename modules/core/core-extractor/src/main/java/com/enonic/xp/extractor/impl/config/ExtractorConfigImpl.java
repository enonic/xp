package com.enonic.xp.extractor.impl.config;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true, configurationPid = "com.enonic.xp.extractor")
public class ExtractorConfigImpl
    implements ExtractorConfig
{

    private final static Logger LOG = LoggerFactory.getLogger( ExtractorConfigImpl.class );

    private boolean enabled;

    private int bodySizeLimit;

    @Override
    public boolean isEnabled()
    {
        return enabled;
    }

    @Override
    public int getBodySizeLimit()
    {
        return bodySizeLimit;
    }

    @Activate
    public void configure( final Map<String, String> config )
    {
        final ExtractorConfigMap configMap = new ExtractorConfigMap( config );

        this.enabled = configMap.isEnabled();
        this.bodySizeLimit = configMap.getBodySizeLimit();

        if ( this.enabled )
        {
            LOG.info( "Binary extractor is enabled and mappings updated." );
        }
    }

}
