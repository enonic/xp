package com.enonic.xp.core.impl.module;

import java.io.IOException;
import java.net.URL;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.Resources;

import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleVersion;
import com.enonic.xp.xml.XmlException;

final class ModuleBuilder
{
    public final static String X_MODULE_URL = "X-Module-Url";

    public final static String X_VENDOR_NAME = "X-Vendor-Name";

    public final static String X_VENDOR_URL = "X-Vendor-Url";

    public final static String X_SYSTEM_VERSION = "X-System-Version";

    private static final String MODULE_XML = "site.xml";

    private Bundle bundle;

    public ModuleBuilder bundle( final Bundle value )
    {
        this.bundle = value;
        return this;
    }

    public Module build()
    {
        final ModuleImpl module = new ModuleImpl();
        module.bundle = this.bundle;

        module.moduleKey = ModuleKey.from( this.bundle );
        module.moduleVersion = ModuleVersion.from( this.bundle.getVersion().toString() );
        module.displayName = getHeader( this.bundle, Constants.BUNDLE_NAME, module.getKey().toString() );
        module.url = getHeader( this.bundle, X_MODULE_URL, null );
        module.vendorName = getHeader( this.bundle, X_VENDOR_NAME, null );
        module.vendorUrl = getHeader( this.bundle, X_VENDOR_URL, null );
        module.systemVersion = getHeader( this.bundle, X_SYSTEM_VERSION, null );
        module.classLoader = new BundleClassLoader( this.bundle );

        readXmlDescriptor( module );

        return module;
    }

    private static void readXmlDescriptor( final ModuleImpl module )
    {
        final URL url = module.bundle.getResource( MODULE_XML );

        try
        {
            final String xml = parseModuleXml( url );

            final XmlModuleParser parser = new XmlModuleParser();
            parser.module( module );
            parser.source( xml );
            parser.parse();
            ;
        }
        catch ( final Exception e )
        {
            throw new XmlException( e, "Invalid site.xml file [bundle:" + module.bundle.getSymbolicName() + ":" + url.getFile() +
                "]: " + e.getMessage() );
        }
    }

    private static String parseModuleXml( final URL moduleResource )
        throws IOException
    {
       return Resources.toString( moduleResource, Charsets.UTF_8 );
    }

    public static boolean isModule( final Bundle bundle )
    {
        return ( bundle.getEntry( MODULE_XML ) != null );
    }

    private static String getHeader( final Bundle bundle, final String name, final String defValue )
    {
        final String value = bundle.getHeaders().get( name );
        return Strings.isNullOrEmpty( value ) ? defValue : value;
    }
}
