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

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPublishInfo;
import com.enonic.xp.content.ImportContentParams;
import com.enonic.xp.core.impl.content.serializer.ContentDataSerializer;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.Node;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlList;

import static com.enonic.xp.content.ContentPropertyNames.ORIGIN_PROJECT;
import static com.enonic.xp.content.ContentPropertyNames.PUBLISH_INFO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
public class ImportContentFactoryTest
{
    private ImportContentParams params;

    @Mock
    private Content content;

    @BeforeEach
    public void setUp()
        throws Exception
    {
        Mockito.when( content.getType() ).thenReturn( ContentTypeName.from( "base:folder" ) );
        Mockito.when( content.getId() ).thenReturn( ContentId.from( "contentId" ) );
        Mockito.when( content.getModifier() ).thenReturn( PrincipalKey.from( "user:system:user" ) );
        Mockito.when( content.getCreator() ).thenReturn( PrincipalKey.from( "user:system:user" ) );
        Mockito.when( content.getData() ).thenReturn( new PropertyTree() );
        Mockito.when( content.getPermissions() ).thenReturn( AccessControlList.empty() );
        Mockito.when( content.getPublishInfo() )
            .thenReturn( ContentPublishInfo.create().first( Instant.now() ).from( Instant.now() ).to( Instant.now() ).build() );
    }

    @Test
    public void replaceOriginProject()
        throws Exception
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
    public void removeOriginProject()
        throws Exception
    {
        Mockito.when( content.getOriginProject() ).thenReturn( ProjectName.from( "old-project" ) );

        params =
            ImportContentParams.create().importContent( content ).targetPath( ContentPath.from( ContentPath.ROOT, "content" ) ).build();

        final Node result = createFactory().execute();

        assertFalse( result.data().hasProperty( ORIGIN_PROJECT ) );

    }

    @Test
    public void removePublishInfo()
        throws Exception
    {
        Mockito.when( content.getOriginProject() ).thenReturn( ProjectName.from( "old-project" ) );

        params =
            ImportContentParams.create().importContent( content ).targetPath( ContentPath.from( ContentPath.ROOT, "content" ) ).build();

        final Node result = createFactory().execute();

        assertFalse( result.data().hasProperty( PUBLISH_INFO ) );

    }

    private ContentDataSerializer createContentDataSerializer()
    {
        final PageDescriptorService pageDescriptorService = Mockito.mock( PageDescriptorService.class );
        final PartDescriptorService partDescriptorService = Mockito.mock( PartDescriptorService.class );
        final LayoutDescriptorService layoutDescriptorService = Mockito.mock( LayoutDescriptorService.class );

        return ContentDataSerializer.create()
            .partDescriptorService( partDescriptorService )
            .pageDescriptorService( pageDescriptorService )
            .layoutDescriptorService( layoutDescriptorService )
            .build();
    }

    private ImportContentFactory createFactory()
    {
        return ImportContentFactory.create().params( this.params ).contentDataSerializer( createContentDataSerializer() ).build();
    }
}
