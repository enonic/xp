package com.enonic.xp.core.impl.app;

import java.net.URL;

import org.osgi.framework.Bundle;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.xml.parser.XmlApplicationParser;

final class ApplicationDescriptorBuilder
{
    private static final String APP_DESCRIPTOR_FILENAME = "application.xml";

    private Bundle bundle;

    public ApplicationDescriptorBuilder bundle( final Bundle value )
    {
        this.bundle = value;
        return this;
    }

    public ApplicationDescriptor build()
    {
        final URL url = bundle.getResource( APP_DESCRIPTOR_FILENAME );
        final String xml = parseAppXml( url );

        ApplicationDescriptor.Builder appDescriptorBuilder = ApplicationDescriptor.create();
        final XmlApplicationParser parser = new XmlApplicationParser().
            currentApplication( ApplicationKey.from( bundle ) ).
            appDescriptorBuilder( appDescriptorBuilder ).
            source( xml );
        parser.parse();

        return appDescriptorBuilder.build();
    }

    private String parseAppXml( final URL siteXmlURL )
    {
        try
        {
            return Resources.toString( siteXmlURL, Charsets.UTF_8 );
        }
        catch ( final Exception e )
        {
            throw new RuntimeException( "Invalid " + APP_DESCRIPTOR_FILENAME + " file", e );
        }
    }

}
