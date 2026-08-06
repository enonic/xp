package com.enonic.xp.portal.url;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;


public final class PageUrlParams
    extends AbstractUrlParams<PageUrlParams>
{
    private String id;

    private String path;

    private String projectName;

    private String branch;

    private String baseUrl;

    public String getId()
    {
        return this.id;
    }

    public String getPath()
    {
        return this.path;
    }

    public String getProjectName()
    {
        return projectName;
    }

    public String getBranch()
    {
        return branch;
    }

    public String getBaseUrl()
    {
        return baseUrl;
    }

    public PageUrlParams id( final String value )
    {
        this.id = Strings.emptyToNull( value );
        return this;
    }

    public PageUrlParams path( final String value )
    {
        this.path = Strings.emptyToNull( value );
        return this;
    }

    public PageUrlParams projectName( final String value )
    {
        this.projectName = Strings.emptyToNull( value );
        return this;
    }

    public PageUrlParams branch( final String value )
    {
        this.branch = Strings.emptyToNull( value );
        return this;
    }

    /**
     * Base URL used verbatim as the prefix of the generated URL, followed by the content
     * path relative to the nearest site (the full content path when there is no site):
     * {@code <baseUrl>/<site-relative path>}. When set, base URL resolution from
     * configuration and from the current request is skipped.
     * Empty value is treated as unspecified.
     */
    public PageUrlParams baseUrl( final String value )
    {
        this.baseUrl = Strings.emptyToNull( value );
        return this;
    }

    @Override
    public String toString()
    {
        final MoreObjects.ToStringHelper helper = MoreObjects.toStringHelper( this );
        helper.omitNullValues();
        helper.add( "type", this.getType() );
        helper.add( "params", this.getParams() );
        helper.add( "id", this.id );
        helper.add( "path", this.path );
        helper.add( "project", this.projectName );
        helper.add( "branch", this.branch );
        helper.add( "baseUrl", this.baseUrl );
        return helper.toString();
    }
}
