package com.enonic.xp.lib.content.mapper;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.DuplicateContentsResult;
import com.enonic.xp.testing.serializer.JsonMapGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DuplicateContentsResultMapperTest
{
    @Test
    public void test()
    {
        JsonMapGenerator jsonGenerator = new JsonMapGenerator();

        DuplicateContentsResultMapper mapper = new DuplicateContentsResultMapper( DuplicateContentsResult.create()
                                                                                      .setContentName( "contentName" )
                                                                                      .setSourceContentPath(
                                                                                          ContentPath.from( "contentPath" ) )
                                                                                      .addDuplicated( ContentId.from( "contentId" ) )
                                                                                      .build() );
        mapper.serialize( jsonGenerator );

        JsonNode actualJson = (JsonNode) jsonGenerator.getRoot();

        assertEquals( "contentName", actualJson.get( "contentName" ).asText() );
        assertEquals( "/contentPath", actualJson.get( "sourceContentPath" ).asText() );
        assertEquals( 1, actualJson.get( "duplicatedContents" ).size() );
        assertEquals( "contentId", actualJson.get( "duplicatedContents" ).get( 0 ).asText() );
    }
}
