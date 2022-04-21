package com.enonic.xp.impl.map;

import org.junit.jupiter.api.Test;

import com.enonic.xp.shared.SharedMap;

import static org.junit.jupiter.api.Assertions.assertSame;

class LocalSharedMapServiceTest
{
    @Test
    void getSharedMap() {
        final LocalSharedMapService service = new LocalSharedMapService();
        final SharedMap<Object, Object> map1 = service.getSharedMap( "a" );
        final SharedMap<Object, Object> map2 = service.getSharedMap( "a" );
        assertSame( map1, map2);
    }
}
