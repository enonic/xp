package com.enonic.xp.support.serializer;

import com.fasterxml.jackson.core.JsonFactory;
import com.google.common.annotations.Beta;

@Beta
public class JsonFactoryHolder
{
    public final static JsonFactory DEFAULT_FACTORY = new JsonFactory();
}
