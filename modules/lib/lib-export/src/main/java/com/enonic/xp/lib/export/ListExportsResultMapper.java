package com.enonic.xp.lib.export;

import com.enonic.xp.export.ExportInfo;
import com.enonic.xp.export.ListExportsResult;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class ListExportsResultMapper
    implements MapSerializable
{
    private final ListExportsResult listExportsResult;

    public ListExportsResultMapper( final ListExportsResult listExportsResult )
    {
        this.listExportsResult = listExportsResult;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.array( "exports" );
        for ( final ExportInfo export : listExportsResult )
        {
            gen.map();
            gen.value( "name", export.name() );
            gen.end();
        }
        gen.end();
    }
}
