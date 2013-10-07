package com.enonic.wem.api.item;


import org.junit.Test;

import static com.enonic.wem.api.item.PropertyIndexConfig.newPropertyIndexConfig;

public class ItemTypeTest
{

    @Test
    public void setPropertyIndexConfig()
    {
        ItemType itemType = new ItemType();
        itemType.setPropertyIndexConfig( "my-property-1", newPropertyIndexConfig().enabled( true ).analyzer( "my-analyzer" ).build() );
        itemType.setPropertyIndexConfig( "my-property-2", newPropertyIndexConfig().enabled( false ).build() );

        System.out.println( itemType );
    }
}
