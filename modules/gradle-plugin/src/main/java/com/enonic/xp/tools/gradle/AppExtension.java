package com.enonic.xp.tools.gradle;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.gradle.api.Project;

public class AppExtension
{
    public final static String NAME = "app";

    private final Project project;

    private String name;

    private String displayName;

    private String url;

    private String vendorName;

    private String vendorUrl;

    private String systemVersion = "[6.0,7)";

    private Map<String, String> instructions;

    private File xpHome;

    public AppExtension( final Project project )
    {
        this.project = project;
        this.xpHome = findDefaultHomeDir();
        this.instructions = new HashMap<>();
    }

    public String getName()
    {
        if ( this.name == null )
        {
            return composeDefaultName();
        }
        else
        {
            return this.name;
        }
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public String getDisplayName()
    {
        return this.displayName != null ? this.displayName : getName();
    }

    public void setDisplayName( final String displayName )
    {
        this.displayName = displayName;
    }

    public File getXpHome()
    {
        return this.xpHome;
    }

    public void setXpHome( final File value )
    {
        this.xpHome = value;
    }

    public String getUrl()
    {
        return this.url;
    }

    public void setUrl( final String url )
    {
        this.url = url;
    }

    public String getVendorName()
    {
        return this.vendorName;
    }

    public void setVendorName( final String vendorName )
    {
        this.vendorName = vendorName;
    }

    public String getVendorUrl()
    {
        return this.vendorUrl;
    }

    public void setVendorUrl( final String vendorUrl )
    {
        this.vendorUrl = vendorUrl;
    }

    public String getSystemVersion()
    {
        return this.systemVersion;
    }

    public void setSystemVersion( final String systemVersion )
    {
        this.systemVersion = systemVersion;
    }

    public Map<String, String> getInstructions()
    {
        return this.instructions;
    }

    public void instruction( final String name, final String value )
    {
        final String oldValue = this.instructions.get( name );
        if ( oldValue != null )
        {
            this.instructions.put( name, oldValue + "," + value );
        }
        else
        {
            this.instructions.put( name, value );
        }
    }

    public static AppExtension get( final Project project )
    {
        return project.getExtensions().getByType( AppExtension.class );
    }

    public static AppExtension create( final Project project )
    {
        return project.getExtensions().create( NAME, AppExtension.class, project );
    }

    private static File findDefaultHomeDir()
    {
        String home = System.getProperty( "xp.home" );
        if ( home == null )
        {
            home = System.getenv( "XP_HOME" );
        }

        return home != null ? new File( home ) : null;
    }

    private String composeDefaultName()
    {
        if ( this.project.getGroup().equals( "" ) )
        {
            return this.project.getName();
        }

        return this.project.getGroup().toString() + "." + this.project.getName();
    }
}
