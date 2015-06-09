package com.enonic.xp.core.impl.site;

import java.net.URL;

import org.osgi.framework.Bundle;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.xp.site.SiteDescriptor;

final class SiteDescriptorBuilder
{
    private static final String SITE_DESCRIPTOR_FILENAME = "site.xml";

    private Bundle bundle;

    public SiteDescriptorBuilder bundle( final Bundle value )
    {
        this.bundle = value;
        return this;
    }

    public SiteDescriptor build()
    {
        final URL url = bundle.getResource( SITE_DESCRIPTOR_FILENAME );
        final String xml = parseSiteXml( url );

        SiteDescriptor.Builder siteDescriptorBuilder = SiteDescriptor.create();
        final XmlSiteParser parser = new XmlSiteParser();
        parser.siteDescriptorBuilder( siteDescriptorBuilder );
        parser.source( xml );
        parser.parse();

        return siteDescriptorBuilder.build();
    }

    private static String parseSiteXml( final URL siteXmlURL )
    {
        try
        {
            return Resources.toString( siteXmlURL, Charsets.UTF_8 );
        }
        catch ( final Exception e )
        {
            throw new RuntimeException( "Invalid " + SITE_DESCRIPTOR_FILENAME + " file", e );
        }
    }

    public static boolean isSite( final Bundle bundle )
    {
        return ( bundle.getEntry( SITE_DESCRIPTOR_FILENAME ) != null );
    }
}
