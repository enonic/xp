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

    private final static String ENABLED_PROPERTY = "enabled";

    private final static String BODY_SIZE_LIMIT_PROPERTY = "body.size.limit";

    protected final static int BODY_SIZE_LIMIT_DEFAULT = 500_000;

    private boolean enabled;

    private int bodySizeLimit = BODY_SIZE_LIMIT_DEFAULT;

    public ExtractorConfigImpl()
    {
    }

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
        setEnabled( config.get( ENABLED_PROPERTY ) );
        if ( isEnabled() )
        {
            setBodySizeLimit( config.get( BODY_SIZE_LIMIT_PROPERTY ) );

            LOG.info( "Binary extractor is enabled and mappings updated." );
        }
    }

    private void setEnabled( final String value )
    {
        this.enabled = "true".equals( value );
    }

    private void setBodySizeLimit( final String value )
    {
        this.bodySizeLimit = value != null && value.matches( "\\d+" ) ? Integer.parseInt( value ) : BODY_SIZE_LIMIT_DEFAULT;
    }

}
