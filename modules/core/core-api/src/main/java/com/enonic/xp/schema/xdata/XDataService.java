package com.enonic.xp.schema.xdata;

import com.google.common.annotations.Beta;

import com.enonic.xp.app.ApplicationKey;

@Beta
public interface XDataService
{
    XData getByName( XDataName name );

    XDatas getByNames( XDataNames names );

    XDatas getAll();

    XDatas getByApplication( ApplicationKey applicationKey );
}
