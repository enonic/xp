package com.enonic.xp.core.impl.content;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.ImportContentParams;
import com.enonic.xp.core.impl.content.serializer.ContentDataSerializer;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.Node;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlList;

import static com.enonic.xp.content.ContentPropertyNames.ORIGIN_PROJECT;
import static com.enonic.xp.content.ContentPropertyNames.PUBLISH_INFO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
class ImportContentFactoryTest
{
    private ImportContentParams params;

    @Mock
    private Content content;

    @BeforeEach
    void setUp()
    {
        Mockito.when( content.getType() ).thenReturn( ContentTypeName.from( "base:folder" ) );
        Mockito.when( content.getId() ).thenReturn( ContentId.from( "contentId" ) );
        Mockito.when( content.getModifier() ).thenReturn( PrincipalKey.from( "user:system:user" ) );
        Mockito.when( content.getCreator() ).thenReturn( PrincipalKey.from( "user:system:user" ) );
        Mockito.when( content.getData() ).thenReturn( new PropertyTree() );
        Mockito.when( content.getPermissions() ).thenReturn( AccessControlList.empty() );
        Mockito.when( content.getAttachments() ).thenReturn( Attachments.empty() );
        Mockito.when( content.getPublishInfo() )
            .thenReturn( ContentPublishInfo.create().first( Instant.now() ).from( Instant.now() ).to( Instant.now() ).build() );
    }

    @Test
    void replaceOriginProject()
    {

        Mockito.when( content.getOriginProject() ).thenReturn( ProjectName.from( "old-project" ) );

        params = ImportContentParams.create()
            .importContent( content )
            .targetPath( ContentPath.from( ContentPath.ROOT, "content" ) )
            .originProject( ProjectName.from( "origin-project" ) )
            .build();

        final Node result = createFactory().execute();

        List<String> origin = new ArrayList<>();
        result.data().getStrings( ORIGIN_PROJECT ).forEach( origin::add );

        assertEquals( 1, origin.size() );
        assertEquals( "origin-project", origin.get( 0 ) );

    }

    @Test
    void removeOriginProject()
    {
        Mockito.when( content.getOriginProject() ).thenReturn( ProjectName.from( "old-project" ) );

        params =
            ImportContentParams.create().importContent( content ).targetPath( ContentPath.from( ContentPath.ROOT, "content" ) ).build();

        final Node result = createFactory().execute();

        assertFalse( result.data().hasProperty( ORIGIN_PROJECT ) );

    }

    @Test
    void removePublishInfo()
    {
        Mockito.when( content.getOriginProject() ).thenReturn( ProjectName.from( "old-project" ) );

        params =
            ImportContentParams.create().importContent( content ).targetPath( ContentPath.from( ContentPath.ROOT, "content" ) ).build();

        final Node result = createFactory().execute();

        assertFalse( result.data().hasProperty( PUBLISH_INFO ) );

    }

    private ImportContentFactory createFactory()
    {
        return ImportContentFactory.create().params( this.params ).contentDataSerializer( new ContentDataSerializer() ).build();
    }
}
