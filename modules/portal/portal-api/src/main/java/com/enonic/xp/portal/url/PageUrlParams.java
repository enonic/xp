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
     * <p>
     * The content has to be inside the selected level, or be that level itself. The base URL of
     * the level does not lead to a content elsewhere, so there is no URL for one: see
     * {@link ContentOutOfScopeException}.
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
        helper.add( "base", this.base );
        return helper.toString();
    }
}
