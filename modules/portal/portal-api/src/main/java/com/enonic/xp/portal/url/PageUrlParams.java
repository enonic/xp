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

    private String anchor;

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

    public String getAnchor()
    {
        return anchor;
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
     * path relative to the anchor. When set, base URL resolution from configuration and from
     * the current request is skipped.
     * Empty value is treated as unspecified.
     *
     * @deprecated use {@link #anchor(String)}: a base URL alone does not say which site it
     * belongs to, so the path cannot be made relative to it.
     */
    @Deprecated
    public PageUrlParams baseUrl( final String value )
    {
        this.baseUrl = Strings.emptyToNull( value );
        return this;
    }

    /**
     * Site the URL is anchored at, as an id or a path: the URL is the Base URL configured for
     * that site followed by the content path relative to it, and the site engine address of
     * the site when it has no Base URL configured. {@code "/"} anchors at the project itself,
     * and so do contents outside any site.
     * <p>
     * Without an anchor the URL is anchored at the site of the content, which for a content
     * inside a nested site is that nested site. Configuration of a site is never inherited
     * from a parent site, so the anchor alone decides which Base URL applies.
     * Empty value is treated as unspecified.
     */
    public PageUrlParams anchor( final String value )
    {
        this.anchor = Strings.emptyToNull( value );
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
        helper.add( "anchor", this.anchor );
        return helper.toString();
    }
}
