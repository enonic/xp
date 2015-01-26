package com.enonic.xp.portal.url;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Multimap;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;

public final class PageUrlParams
    extends AbstractUrlParams<PageUrlParams>
{
    private ContentId id;

    private ContentPath path;

    public ContentId getId()
    {
        return this.id;
    }

    public ContentPath getPath()
    {
        return this.path;
    }

    public PageUrlParams id( final ContentId value )
    {
        this.id = value;
        return this;
    }

    public PageUrlParams path( final ContentPath value )
    {
        this.path = value;
        return this;
    }

    public PageUrlParams id( final String value )
    {
        return Strings.isNullOrEmpty( value ) ? this : id( ContentId.from( value ) );
    }

    public PageUrlParams path( final String value )
    {
        return Strings.isNullOrEmpty( value ) ? this : path( ContentPath.from( value ) );
    }

    @Override
    public PageUrlParams setAsMap( final Multimap<String, String> map )
    {
        id( singleValue( map, "_id" ) );
        path( singleValue( map, "_path" ) );
        getParams().putAll( map );
        return this;
    }

    @Override
    protected void buildToString( final Objects.ToStringHelper helper )
    {
        super.buildToString( helper );
        helper.add( "id", this.id );
        helper.add( "path", this.path );
    }
}
