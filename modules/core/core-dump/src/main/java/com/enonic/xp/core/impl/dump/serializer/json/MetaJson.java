package com.enonic.xp.core.impl.dump.serializer.json;

import com.enonic.xp.core.impl.dump.model.Meta;

public class MetaJson
{
    private final String nodePath;

    private final String timestamp;

    private final String version;

    public MetaJson( final Meta meta )
    {
        this.nodePath = meta.getNodePath().toString();
        this.timestamp = meta.getTimestamp().toString();
        this.version = meta.getVersion().toString();
    }

    public String getNodePath()
    {
        return nodePath;
    }

    public String getTimestamp()
    {
        return timestamp;
    }

    public String getVersion()
    {
        return version;
    }
}
