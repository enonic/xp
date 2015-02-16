package com.enonic.xp.portal.impl.jslib.mapper;

import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.Metadata;
import com.enonic.xp.content.page.Page;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.portal.script.serializer.MapGenerator;
import com.enonic.xp.portal.script.serializer.MapSerializable;

public final class ContentMapper
    implements MapSerializable
{
    private final Content value;

    public ContentMapper( final Content value )
    {
        this.value = value;
    }

    private static void serialize( final MapGenerator gen, final Content value )
    {
        gen.value( "_id", value.getId() );
        gen.value( "_name", value.getName() );
        gen.value( "_path", value.getPath() );
        gen.value( "creator", value.getCreator() );
        gen.value( "modifier", value.getModifier() );
        gen.value( "createdTime", value.getCreatedTime() );
        gen.value( "modifiedTime", value.getModifiedTime() );
        gen.value( "type", value.getType() );
        gen.value( "displayName", value.getDisplayName() );
        gen.value( "hasChildren", value.hasChildren() );
        gen.value( "valid", value.isValid() );

        serializeData( gen, value.getData() );
        serializeMetaData( gen, value.getAllMetadata() );
        serializePage( gen, value.getPage() );
    }

    private static void serializeData( final MapGenerator gen, final PropertyTree value )
    {
        gen.map( "data" );
        new PropertyTreeMapper( value ).serialize( gen );
        gen.end();
    }

    private static void serializeMetaData( final MapGenerator gen, final Iterable<Metadata> values )
    {
        gen.map( "x" );

        final ListMultimap<ModuleKey, Metadata> metadatasByModule = ArrayListMultimap.create();
        for ( Metadata metadata : values )
        {
            metadatasByModule.put( metadata.getName().getModuleKey(), metadata );
        }

        for ( final ModuleKey moduleKey : metadatasByModule.keys() )
        {
            final List<Metadata> metadatas = metadatasByModule.get( moduleKey );
            if ( metadatas.isEmpty() )
            {
                continue;
            }
            gen.map( metadatas.get( 0 ).getModulePrefix() );
            for ( final Metadata metadata : metadatas )
            {
                gen.map( metadata.getName().getLocalName() );
                new PropertyTreeMapper( metadata.getData() ).serialize( gen );
                gen.end();
            }
            gen.end();
        }
        gen.end();
    }

    private static void serializePage( final MapGenerator gen, final Page value )
    {
        gen.map( "page" );
        if ( value != null )
        {
            new PageMapper( value ).serialize( gen );
        }
        gen.end();
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        serialize( gen, this.value );
    }
}
