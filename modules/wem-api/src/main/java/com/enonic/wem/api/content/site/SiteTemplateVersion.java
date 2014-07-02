package com.enonic.wem.api.content.site;

import com.enonic.wem.api.BaseVersion;

public final class SiteTemplateVersion
    extends BaseVersion
    implements Comparable<SiteTemplateVersion>
{
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
}
