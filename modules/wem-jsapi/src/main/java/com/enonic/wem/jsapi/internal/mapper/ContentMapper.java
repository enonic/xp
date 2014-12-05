package com.enonic.wem.jsapi.internal.mapper;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.Metadata;
import com.enonic.wem.script.serializer.MapGenerator;
import com.enonic.wem.script.serializer.MapSerializable;

final class ContentMapper
    implements MapSerializable
{
    private final Content value;

    public ContentMapper( final Content value )
    {
        this.value = value;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "_id", this.value.getId() );
        gen.value( "_name", this.value.getName() );
        gen.value( "_path", this.value.getPath() );
        gen.value( "_creator", this.value.getCreator() );
        gen.value( "_modifier", this.value.getModifier() );
        gen.value( "_createdTime", this.value.getCreatedTime() );
        gen.value( "_modifiedTime", this.value.getModifiedTime() );
        gen.value( "type", this.value.getType() );
        gen.value( "displayName", this.value.getDisplayName() );

        serializeData( gen );
        serializeMetaData( gen );
        serializePage( gen );
    }

    private void serializeData( final MapGenerator gen )
    {
        gen.value( "data", ResultMappers.mapper( this.value.getData() ) );
    }

    private void serializeMetaData( final MapGenerator gen )
    {
        gen.map( "metadata" );
        for ( final Metadata metadata : this.value.getAllMetadata() )
        {
            gen.value( metadata.getName().toString(), ResultMappers.mapper( metadata.getData() ) );
        }
        gen.end();
    }

    private void serializePage( final MapGenerator gen )
    {
        gen.value( "page", ResultMappers.mapper( this.value.getPage() ) );
    }
}
