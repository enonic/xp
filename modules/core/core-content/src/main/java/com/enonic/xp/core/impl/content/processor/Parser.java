package com.enonic.xp.core.impl.content.processor;

public interface Parser<RESULT>
{
    RESULT parse( final String value );
}
