package com.enonic.wem.api.content.page;

import com.enonic.wem.api.data.RootDataSet;

public class Layout
    extends Component
{
    RootDataSet liveEditConfig;

    /**
     * Values will override any values in LayoutTemplate.pageConfig.
     */
    RootDataSet layoutConfig;
}
