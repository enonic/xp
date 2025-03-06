package com.enonic.xp.portal.url;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.Multimap;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class PageUrlParams
    extends AbstractUrlParams<PageUrlParams>
{
    private String id;

    private String path;

    private String projectName;

    private String branch;

    private String baseUrl;

    private BaseUrlParams baseUrlParams;

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

    public BaseUrlParams getBaseUrlParams()
    {
        return baseUrlParams;
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

    public PageUrlParams baseUrlParams( final BaseUrlParams baseUrlParams )
    {
        this.baseUrlParams = baseUrlParams;
        return this;
    }

    public PageUrlParams baseUrl( final String baseUrl )
    {
        this.baseUrl = baseUrl;
        return this;
    }

    @Override
    public PageUrlParams setAsMap( final Multimap<String, String> map )
    {
        super.setAsMap( map );
        id( singleValue( map, "_id" ) );
        path( singleValue( map, "_path" ) );
        getParams().putAll( map );
        return this;
    }

    @Override
    protected void buildToString( final MoreObjects.ToStringHelper helper )
    {
        super.buildToString( helper );
        helper.add( "id", this.id );
        helper.add( "path", this.path );
    }
}
