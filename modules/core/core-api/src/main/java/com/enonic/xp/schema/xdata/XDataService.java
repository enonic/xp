package com.enonic.xp.schema.xdata;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;

@PublicApi
public interface XDataService
{
    XData getByName( XDataName name );

    XDatas getByNames( XDataNames names );

    XDatas getAll();

    XDatas getByApplication( ApplicationKey applicationKey );
}
