package com.enonic.xp.admin.impl.json.schema.xdata;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.admin.impl.rest.resource.schema.content.LocaleMessageResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.mixin.MixinIconUrlResolver;
import com.enonic.xp.schema.xdata.XDatas;

public class XDataListJson
{
    private final List<XDataJson> list;

    public XDataListJson()
    {
        this.list = new ArrayList<>();
    }


    public XDataListJson( final List<XDataJson> list )
    {
        this.list = new ArrayList<>( list );
    }

    public XDataListJson( final XDatas xDatas, final MixinIconUrlResolver iconUrlResolver,
                          final LocaleMessageResolver localeMessageResolver )
    {
        this.list = xDatas.stream().map(
            xData -> XDataJson.create().setXData( xData ).setIconUrlResolver( iconUrlResolver ).setLocaleMessageResolver(
                localeMessageResolver ).build() ).collect( Collectors.toList() );
    }

    public void addXDatas( final List<XDataJson> xDatas )
    {
        this.list.addAll( xDatas );
    }

    // TODO rename to getXData, requires changes in front-end code
    public List<XDataJson> getMixins()
    {
        return this.list;
    }
}
