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

    private BaseUrlParams base;

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

    public BaseUrlParams getBase()
    {
        return base;
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
     * path relative to the site the URL belongs to. When set, base URL resolution from
     * configuration and from the current request is skipped.
     * Empty value is treated as unspecified.
     *
     * @deprecated use {@link #base(BaseUrlParams)}: a base URL alone does not say which site it
     * belongs to, so the path cannot be made relative to it.
     */
    @Deprecated
    public PageUrlParams baseUrl( final String value )
    {
        this.baseUrl = Strings.emptyToNull( value );
        return this;
    }

    /**
     * Selects the site - or the project - the URL belongs to, by the same parameters
     * {@link PortalUrlService#baseUrl(BaseUrlParams)} takes: the nearest one at or above the
     * content they name. The URL then starts with the base URL that one resolves to, followed by
     * the content path relative to it. Passing the very same parameters to both calls is what
     * makes {@code pageUrl = baseUrl + path + queryString} hold.
     * <p>
     * A project contains sites and a site can contain further sites, so this picks a level of
     * that containment: {@code "/"} names the project, a site path or id names that site.
     * Without it the URL belongs to the innermost level containing the content. Configuration is
     * never inherited from a level above, so the selected one alone decides which Base URL
     * applies.
     */
    public PageUrlParams base( final BaseUrlParams value )
    {
        this.base = value;
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
        helper.add( "base", this.base );
        return helper.toString();
    }
}
