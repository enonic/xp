package com.enonic.xp.repo.impl.vacuum.version;

import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.internal.blobstore.MemoryBlobRecord;
import com.enonic.xp.node.NodeId;

import static org.junit.Assert.*;

public class NodeIdResolverImplTest
{
    @Test
    public void resolve()
        throws Exception
    {
        final NodeIdResolverImpl resolver = new NodeIdResolverImpl();

        final MemoryBlobRecord record = new MemoryBlobRecord( BlobKey.from( "a" ), ByteSource.wrap( createNodeEntry() ) );

        final NodeId resolvedId = resolver.resolve( record );

        assertNotNull( resolvedId );
        assertEquals( NodeId.from( "b2d88930-6aa2-45a4-bd8a-5df5c98c28b2" ), resolvedId );
    }

    @Test
    public void not_json()
        throws Exception
    {
        final NodeIdResolverImpl resolver = new NodeIdResolverImpl();

        final MemoryBlobRecord record =
            new MemoryBlobRecord( BlobKey.from( "a" ), ByteSource.wrap( "some text".getBytes( StandardCharsets.UTF_8 ) ) );

        final NodeId resolvedId = resolver.resolve( record );

        assertNull( resolvedId );
    }

    @Test
    public void json_without_id()
        throws Exception
    {
        final NodeIdResolverImpl resolver = new NodeIdResolverImpl();

        final MemoryBlobRecord record = new MemoryBlobRecord( BlobKey.from( "a" ), ByteSource.wrap(
            "{\"attachedBinaries\":[],\"childOrder\":\"modifiedtime DESC\"}".getBytes( StandardCharsets.UTF_8 ) ) );

        final NodeId resolvedId = resolver.resolve( record );

        assertNull( resolvedId );
    }

    private byte[] createNodeEntry()
    {
        return "{\"attachedBinaries\":[],\"childOrder\":\"modifiedtime DESC\",\"data\":[{\"name\":\"valid\",\"type\":\"Boolean\",\"values\":[{\"v\":true}]},{\"name\":\"displayName\",\"type\":\"String\",\"values\":[{\"v\":\"Arild Grande\"}]},{\"name\":\"type\",\"type\":\"String\",\"values\":[{\"v\":\"no.rett24.web:featured-person\"}]},{\"name\":\"owner\",\"type\":\"String\",\"values\":[{\"v\":\"user:system:su\"}]},{\"name\":\"modifiedTime\",\"type\":\"DateTime\",\"values\":[{\"v\":\"2017-07-16T15:40:02.459Z\"}]},{\"name\":\"modifier\",\"type\":\"String\",\"values\":[{\"v\":\"user:system:su\"}]},{\"name\":\"creator\",\"type\":\"String\",\"values\":[{\"v\":\"user:system:su\"}]},{\"name\":\"createdTime\",\"type\":\"DateTime\",\"values\":[{\"v\":\"2017-07-16T15:39:08.168Z\"}]},{\"name\":\"publish\",\"type\":\"PropertySet\",\"values\":[{\"set\":[{\"name\":\"first\",\"type\":\"DateTime\",\"values\":[{}]},{\"name\":\"from\",\"type\":\"DateTime\",\"values\":[{}]},{\"name\":\"to\",\"type\":\"DateTime\",\"values\":[{}]}]}]},{\"name\":\"data\",\"type\":\"PropertySet\",\"values\":[{\"set\":[{\"name\":\"author\",\"type\":\"Reference\",\"values\":[{\"v\":\"895a4819-293f-4e85-946b-33ecc9f117d7\"}]},{\"name\":\"category\",\"type\":\"Reference\",\"values\":[]},{\"name\":\"titleImage\",\"type\":\"Reference\",\"values\":[{\"v\":\"6406ffd0-40f3-4551-88fc-dcf78025b78d\"}]},{\"name\":\"title\",\"type\":\"String\",\"values\":[{\"v\":\"Arild Grande\"}]},{\"name\":\"titleShort\",\"type\":\"String\",\"values\":[{}]},{\"name\":\"ingress\",\"type\":\"String\",\"values\":[{\"v\":\"Går til skaniabanken\"}]},{\"name\":\"body\",\"type\":\"String\",\"values\":[{\"v\":\"<p style=\\\"padding-left: 30px;\\\">Fisk ost p&oslash;lse</p>\"}]},{\"name\":\"tags\",\"type\":\"String\",\"values\":[]},{\"name\":\"itemStyle\",\"type\":\"String\",\"values\":[{\"v\":\"item__light\"}]}]}]}],\"id\":\"b2d88930-6aa2-45a4-bd8a-5df5c98c28b2\",\"indexConfigDocument\":{\"analyzer\":\"document_index_default\",\"defaultConfig\":{\"decideByType\":true,\"enabled\":true,\"fulltext\":false,\"includeInAllText\":false,\"nGram\":false},\"patternConfigs\":[{\"indexConfig\":{\"decideByType\":false,\"enabled\":true,\"fulltext\":true,\"includeInAllText\":true,\"indexValueProcessors\":[\"htmlStripper\"],\"nGram\":true},\"path\":\"page.region.**.TextComponent.text\"},{\"indexConfig\":{\"decideByType\":false,\"enabled\":true,\"fulltext\":false,\"includeInAllText\":false,\"nGram\":false},\"path\":\"data.siteconfig.applicationkey\"},{\"indexConfig\":{\"decideByType\":false,\"enabled\":true,\"fulltext\":true,\"includeInAllText\":false,\"nGram\":true},\"path\":\"attachment.text\"},{\"indexConfig\":{\"decideByType\":false,\"enabled\":false,\"fulltext\":false,\"includeInAllText\":false,\"nGram\":false},\"path\":\"page.regions\"},{\"indexConfig\":{\"decideByType\":false,\"enabled\":true,\"fulltext\":false,\"includeInAllText\":false,\"nGram\":false},\"path\":\"modifier\"},{\"indexConfig\":{\"decideByType\":false,\"enabled\":true,\"fulltext\":false,\"includeInAllText\":false,\"nGram\":false},\"path\":\"owner\"},{\"indexConfig\":{\"decideByType\":false,\"enabled\":true,\"fulltext\":false,\"includeInAllText\":false,\"nGram\":false},\"path\":\"attachment\"},{\"indexConfig\":{\"decideByType\":false,\"enabled\":true,\"fulltext\":false,\"includeInAllText\":false,\"nGram\":false},\"path\":\"x\"},{\"indexConfig\":{\"decideByType\":false,\"enabled\":false,\"fulltext\":false,\"includeInAllText\":false,\"nGram\":false},\"path\":\"page\"},{\"indexConfig\":{\"decideByType\":false,\"enabled\":false,\"fulltext\":false,\"includeInAllText\":false,\"nGram\":false},\"path\":\"site\"},{\"indexConfig\":{\"decideByType\":false,\"enabled\":true,\"fulltext\":false,\"includeInAllText\":false,\"nGram\":false},\"path\":\"type\"},{\"indexConfig\":{\"decideByType\":true,\"enabled\":true,\"fulltext\":false,\"includeInAllText\":false,\"nGram\":false},\"path\":\"data\"},{\"indexConfig\":{\"decideByType\":false,\"enabled\":true,\"fulltext\":false,\"includeInAllText\":false,\"nGram\":false},\"path\":\"creator\"},{\"indexConfig\":{\"decideByType\":false,\"enabled\":true,\"fulltext\":false,\"includeInAllText\":false,\"nGram\":false},\"path\":\"createdTime\"},{\"indexConfig\":{\"decideByType\":false,\"enabled\":true,\"fulltext\":false,\"includeInAllText\":false,\"nGram\":false},\"path\":\"modifiedTime\"}]},\"inheritPermissions\":true,\"manualOrderValue\":null,\"nodeType\":\"content\",\"permissions\":[{\"allow\":[\"READ\"],\"deny\":[],\"principal\":\"role:cms.cm.app\"},{\"allow\":[\"READ\",\"CREATE\",\"MODIFY\",\"DELETE\",\"PUBLISH\",\"READ_PERMISSIONS\",\"WRITE_PERMISSIONS\"],\"deny\":[],\"principal\":\"role:system.admin\"},{\"allow\":[\"READ\",\"CREATE\",\"MODIFY\",\"DELETE\",\"PUBLISH\",\"READ_PERMISSIONS\",\"WRITE_PERMISSIONS\"],\"deny\":[],\"principal\":\"role:cms.admin\"}],\"timestamp\":\"2017-07-16T15:40:02.460Z\"};\n".
            getBytes( StandardCharsets.UTF_8 );
    }
}