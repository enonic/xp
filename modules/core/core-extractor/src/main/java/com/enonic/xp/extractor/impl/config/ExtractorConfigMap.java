package com.enonic.xp.extractor.impl.config;

import java.util.Map;

final class ExtractorConfigMap
{
    private final static String BODY_SIZE_LIMIT_PROPERTY = "body.size.limit";

    protected final static int BODY_SIZE_LIMIT_DEFAULT = 500_000;

    private final Map<String, String> map;

    ExtractorConfigMap( final Map<String, String> map )
    {
        this.map = map;
    }

    int getBodySizeLimit()
    {
        final String value = map.get( BODY_SIZE_LIMIT_PROPERTY );
        return value != null && value.matches( "\\d+" ) ? Integer.parseInt( value ) : BODY_SIZE_LIMIT_DEFAULT;
    }

}
