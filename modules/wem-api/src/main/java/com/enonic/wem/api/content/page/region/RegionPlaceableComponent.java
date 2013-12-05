package com.enonic.wem.api.content.page.region;


import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.rendering.Renderable;

public interface RegionPlaceableComponent
    extends Renderable
{

    DataSet toDataSet();
}
