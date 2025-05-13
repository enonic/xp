package com.enonic.xp.portal.url;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;

import com.enonic.xp.annotation.PublicApi;

import static com.google.common.base.Strings.isNullOrEmpty;

@PublicApi
public final class AttachmentUrlParams
    extends AbstractUrlParams<AttachmentUrlParams>
{
    private String id;

    private String path;

    private String name;

    private String label;

    private boolean download;

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

    public String getName()
    {
        return this.name;
    }

    public String getLabel()
    {
        return this.label;
    }

    public boolean isDownload()
    {
        return this.download;
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

    public AttachmentUrlParams id( final String value )
    {
        this.id = Strings.emptyToNull( value );
        return this;
    }

    public AttachmentUrlParams path( final String value )
    {
        this.path = Strings.emptyToNull( value );
        return this;
    }

    public AttachmentUrlParams name( final String value )
    {
        this.name = Strings.emptyToNull( value );
        return this;
    }

    public AttachmentUrlParams label( final String value )
    {
        this.label = Strings.emptyToNull( value );
        return this;
    }

    public AttachmentUrlParams download( final String value )
    {
        return isNullOrEmpty( value ) ? this : download( "true".equals( value ) );
    }

    public AttachmentUrlParams download( final boolean value )
    {
        this.download = value;
        return this;
    }

    public AttachmentUrlParams projectName( final String projectName )
    {
        this.projectName = projectName;
        return this;
    }

    public AttachmentUrlParams branch( final String branch )
    {
        this.branch = branch;
        return this;
    }

    public AttachmentUrlParams baseUrl( final String baseUrl )
    {
        this.baseUrl = baseUrl;
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
        helper.add( "name", this.name );
        helper.add( "label", this.label );
        helper.add( "download", this.download );
        return helper.toString();
    }
}
