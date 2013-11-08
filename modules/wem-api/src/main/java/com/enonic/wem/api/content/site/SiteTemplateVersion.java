package com.enonic.wem.api.content.site;

import com.enonic.wem.api.BaseVersion;

public final class SiteTemplateVersion
    extends BaseVersion
    implements Comparable<SiteTemplateVersion>
{
    private SiteTemplateVersion( final int major, final int minor, final int revision )
    {
        super( major, minor, revision );
    }

    public SiteTemplateVersion( final String version )
    {
        super( version );
    }

    @Override
    public int compareTo( final SiteTemplateVersion other )
    {
        return super.compareTo( other );
    }

    public static SiteTemplateVersion from( final String version )
    {
        return new SiteTemplateVersion( version );
    }

    public static SiteTemplateVersion from( final int major, final int minor, final int revision )
    {
        return new SiteTemplateVersion( major, minor, revision );
    }
}
