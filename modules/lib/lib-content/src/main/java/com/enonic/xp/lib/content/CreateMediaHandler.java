package com.enonic.xp.lib.content;

import com.google.common.io.ByteSource;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateMediaParams;
import com.enonic.xp.lib.content.mapper.ContentMapper;

public final class CreateMediaHandler
    extends BaseContextHandler
{
    private String name;

    private String parentPath;

    private ByteSource data;

    private String mimeType;

    private double focalX = 0.5;

    private double focalY = 0.5;

    @Override
    protected Object doExecute()
    {
        final CreateMediaParams params = createParams();
        final Content result = this.contentService.create( params );
        return new ContentMapper( result );
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public void setParentPath( final String parentPath )
    {
        this.parentPath = parentPath;
    }

    public void setData( final ByteSource data )
    {
        this.data = data;
    }

    public void setMimeType( final String mimeType )
    {
        this.mimeType = mimeType;
    }

    public void setFocalX( final double focalX )
    {
        this.focalX = focalX;
    }

    public void setFocalY( final double focalY )
    {
        this.focalY = focalY;
    }

    private CreateMediaParams createParams()
    {
        final CreateMediaParams params = new CreateMediaParams();
        params.name( this.name );
        params.parent( this.parentPath != null ? ContentPath.from( this.parentPath ) : null );
        params.mimeType( this.mimeType );
        params.byteSource( this.data );
        params.focalX( this.focalX );
        params.focalY( this.focalY );
        return params;
    }
}
