package com.enonic.wem.api.content.site;

import org.apache.commons.lang.StringUtils;

import static com.google.common.base.Preconditions.checkNotNull;


public final class SiteTemplateKey
{
    private static final String SEPARATOR = "-";

    private final SiteTemplateName name;

    private final SiteTemplateVersion version;

    private final String refString;

    private SiteTemplateKey( final SiteTemplateName name, final SiteTemplateVersion version )
    {
        checkNotNull( name );
        checkNotNull( version );
        this.name = name;
        this.version = version;
        this.refString = name.toString() + SEPARATOR + version.toString();
    }

    public SiteTemplateName getName()
    {
        return name;
    }

    public SiteTemplateVersion getVersion()
    {
        return version;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || !( o instanceof SiteTemplateKey ) )
        {
            return false;
        }
        final SiteTemplateKey that = (SiteTemplateKey) o;
        return refString.equals( that.refString );
    }

    @Override
    public int hashCode()
    {
        return refString.hashCode();
    }

    @Override
    public String toString()
    {
        return refString;
    }

    public static SiteTemplateKey from( final SiteTemplateName name, final SiteTemplateVersion version )
    {
        return new SiteTemplateKey( name, version );
    }

    public static SiteTemplateKey from( final String siteTemplateKey )
    {
        final String name = StringUtils.substringBeforeLast( siteTemplateKey, SEPARATOR );
        final String version = StringUtils.substringAfterLast( siteTemplateKey, SEPARATOR );
        return new SiteTemplateKey( new SiteTemplateName( name ), SiteTemplateVersion.from( version ) );
    }
}
