package com.enonic.wem.api.item;

import com.enonic.wem.api.data.DataPath;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;

public class ItemType
{
    private RootDataSet rootDataSet = new RootDataSet();

    public void setPropertyIndexConfig( final String path, final PropertyIndexConfig config )
    {
        final DataPath parentPath = DataPath.from( path );

        rootDataSet.setProperty( DataPath.from( parentPath, "enabled" ), new Value.String( java.lang.String.valueOf( config.enabled() ) ) );

    }
}
