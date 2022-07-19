package com.enonic.xp.lib.app.mapper;

import java.util.Base64;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class ApplicationMapper
    implements MapSerializable
{
    private final Application application;

    private final ApplicationDescriptor descriptor;

    public ApplicationMapper( final Application application, final ApplicationDescriptor descriptor )
    {
        this.application = application;
        this.descriptor = descriptor;
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
        gen.value( "minSystemVersion", application.getMinSystemVersion() );
        gen.value( "maxSystemVersion", application.getMaxSystemVersion() );
        gen.value( "modifiedTime", application.getModifiedTime() );
        gen.value( "started", application.isStarted() );
        gen.value( "description", descriptor != null ? descriptor.getDescription() : null );
        gen.value( "icon", getApplicationIcon() );
    }

    private String getApplicationIcon()
    {
        final Icon icon = descriptor != null ? descriptor.getIcon() : null;

        if ( icon != null && icon.getMimeType() != null )
        {
            return "data:" + icon.getMimeType() + ";base64, " + Base64.getEncoder().encodeToString( icon.toByteArray() );
        }

        return null;
    }
}
