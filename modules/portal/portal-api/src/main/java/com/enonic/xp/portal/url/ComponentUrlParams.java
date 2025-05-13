package com.enonic.xp.portal.url;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class ComponentUrlParams
    extends AbstractUrlParams<ComponentUrlParams>
{
    private String id;

    private String path;

    private String component;

    private String projectName;

    private String branch;

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

    public String getComponent()
    {
        return this.component;
    }

    public ComponentUrlParams id( final String value )
    {
        this.id = Strings.emptyToNull( value );
        return this;
    }

    public ComponentUrlParams path( final String value )
    {
        this.path = Strings.emptyToNull( value );
        return this;
    }

    public ComponentUrlParams component( final String value )
    {
        this.component = Strings.emptyToNull( value );
        return this;
    }

    public ComponentUrlParams projectName( final String projectName )
    {
        this.projectName = projectName;
        return this;
    }

    public ComponentUrlParams branch( final String branch )
    {
        this.branch = branch;
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
        helper.add( "component", this.component );
        return helper.toString();
    }
}
