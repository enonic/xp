package com.enonic.wem.jsapi.internal.mapper;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.script.serializer.MapGenerator;
import com.enonic.wem.script.serializer.MapSerializable;

public final class ContentsResultMapper
    implements MapSerializable
{
    private final Contents contents;

    private final long total;

    public ContentsResultMapper( final Contents contents, final long total )
    {
        this.contents = contents;
        this.total = total;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "total", this.total );
        serialize( gen, this.contents );
    }

    private void serialize( final MapGenerator gen, final Contents contents )
    {
        gen.array( "contents" );
        for ( Content content : contents )
        {
            gen.map();
            new ContentMapper( content ).serialize( gen );
            gen.end();
        }
        gen.end();
    }

}
