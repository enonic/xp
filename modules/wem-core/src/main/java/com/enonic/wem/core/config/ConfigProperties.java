package com.enonic.wem.core.config;

import java.util.Map;

import com.google.common.base.Predicate;

public interface ConfigProperties
    extends Map<String, String>
{
    public ConfigProperties getSubConfig( Predicate<String> predicate );
}
