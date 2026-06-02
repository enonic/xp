package com.enonic.xp.repo.impl.dump.serializer.json;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import com.enonic.xp.repo.impl.dump.model.DumpMeta;
import com.enonic.xp.util.Version;

import static org.assertj.core.api.Assertions.assertThat;

class DumpMetaJsonSerializerTest
{
    @Test
    void missing_model_version_deserializes_to_null()
    {
        final String json = "{\"xpVersion\":\"7.16.4\",\"timestamp\":\"2022-06-07T11:53:40.088Z\"}";

        final DumpMeta dumpMeta =
            new DumpMetaJsonSerializer().toDumpMeta( new ByteArrayInputStream( json.getBytes( StandardCharsets.UTF_8 ) ) );

        assertThat( dumpMeta.getModelVersion() ).isNull();
    }

    @Test
    void present_model_version_is_read()
    {
        final String json = "{\"xpVersion\":\"7.16.4\",\"timestamp\":\"2022-06-07T11:53:40.088Z\",\"modelVersion\":\"8\"}";

        final DumpMeta dumpMeta =
            new DumpMetaJsonSerializer().toDumpMeta( new ByteArrayInputStream( json.getBytes( StandardCharsets.UTF_8 ) ) );

        assertThat( dumpMeta.getModelVersion() ).isEqualTo( new Version( 8, 0, 0 ) );
    }
}
