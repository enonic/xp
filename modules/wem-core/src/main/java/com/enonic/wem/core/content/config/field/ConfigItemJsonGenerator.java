package com.enonic.wem.core.content.config.field;


import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;

public abstract class ConfigItemJsonGenerator
{
    public abstract void generate( ConfigItem configItem, JsonGenerator g )
        throws IOException;
}
