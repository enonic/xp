package com.enonic.xp.portal.url;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Multimap;

public final class AttachmentUrlParams
    extends AbstractUrlParams<AttachmentUrlParams>
{
    private String id;

    private String path;

    private String name;

    private String label;

    private boolean download = false;

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
        return Strings.isNullOrEmpty( value ) ? this : download( "true".equals( value ) );
    }

    public AttachmentUrlParams download( final boolean value )
    {
        this.download = value;
        return this;
    }

    @Override
    public AttachmentUrlParams setAsMap( final Multimap<String, String> map )
    {
        id( singleValue( map, "_id" ) );
        path( singleValue( map, "_path" ) );
        name( singleValue( map, "_name" ) );
        label( singleValue( map, "_label" ) );
        download( singleValue( map, "_download" ) );
        getParams().putAll( map );
        return this;
    }

    @Override
    protected void buildToString( final Objects.ToStringHelper helper )
    {
        super.buildToString( helper );
        helper.add( "id", this.id );
        helper.add( "path", this.path );
        helper.add( "name", this.name );
        helper.add( "label", this.label );
        helper.add( "download", this.download );
    }
}
