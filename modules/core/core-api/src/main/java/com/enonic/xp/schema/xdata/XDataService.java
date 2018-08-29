package com.enonic.xp.schema.xdata;

import com.google.common.annotations.Beta;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.schema.content.ContentType;

@Beta
public interface XDataService
{
    XData getByName( XDataName name );

    XDatas getByNames( XDataNames names );

    XDatas getAll();

    XDatas getByApplication( ApplicationKey applicationKey );

    XDatas getFromContentType( final ContentType contentType );
}
