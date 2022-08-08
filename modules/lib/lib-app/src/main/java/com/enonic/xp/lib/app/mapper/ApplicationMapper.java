package com.enonic.xp.lib.app.mapper;

import com.enonic.xp.app.Application;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class ApplicationMapper
    implements MapSerializable
{
    private final Application application;

    public ApplicationMapper( final Application application )
    {
        this.application = application;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "key", application.getKey() );
        gen.value( "displayName", application.getDisplayName() );
        gen.value( "vendorName", application.getVendorName() );
        gen.value( "vendorUrl", application.getVendorUrl() );
        gen.value( "url", application.getUrl() );
        gen.value( "version", application.getVersion() );
        gen.value( "systemVersion", application.getSystemVersion() );
        gen.value( "minSystemVersion", application.getMinSystemVersion() );
        gen.value( "maxSystemVersion", application.getMaxSystemVersion() );
        gen.value( "modifiedTime", application.getModifiedTime() );
        gen.value( "started", application.isStarted() );
        gen.value( "system", application.isSystem() );
    }
}
