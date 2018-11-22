package com.enonic.xp.core.content;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.core.impl.content.ContentOutboundDependenciesIdsResolver;
import com.enonic.xp.core.impl.content.serializer.ContentDataSerializer;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.util.Reference;


public class ContentOutboundDependenciesIdsResolverTest
{

    private ContentService contentService;

    private ContentOutboundDependenciesIdsResolver resolver;

    private ContentDataSerializer contentDataSerializer;

    @Before
    public void setUp()
        throws Exception
    {
        this.contentService = Mockito.mock( ContentService.class );
        this.contentDataSerializer = Mockito.mock( ContentDataSerializer.class );
        this.resolver = new ContentOutboundDependenciesIdsResolver( contentService, contentDataSerializer );
    }

    private Content createContent( final String id, final PropertyTree data, final ContentTypeName contentTypeName )
    {
        return this.createContent( id, data, contentTypeName, ExtraDatas.empty() );
    }

    private Content createContent( final String id, final PropertyTree data, final ContentTypeName contentTypeName,
                                   final ExtraDatas extraDatas )
    {
        return Content.create().
            id( ContentId.from( id ) ).
            data( data ).
            parentPath( ContentPath.ROOT ).
            name( id ).
            valid( true ).
            creator( PrincipalKey.from( "user:system:admin" ) ).
            owner( PrincipalKey.from( "user:myStore:me" ) ).
            language( Locale.ENGLISH ).
            extraDatas( extraDatas ).
            displayName( "My Content" ).
            modifier( PrincipalKey.from( "user:system:admin" ) ).
            type( contentTypeName ).
            build();
    }

    @Test
    public void resolve_outbound_dependencies()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();

        final Content folderRefContent1 = createContent( "folderRefContent1", data, ContentTypeName.folder() );

        final Content folderRefContent2 = createContent( "folderRefContent2", data, ContentTypeName.folder() );

        final Content siteRefContent1 = createContent( "siteRefContent1", data, ContentTypeName.site() );

        data.addReference( "myRef1", Reference.from( folderRefContent1.getId().toString() ) );
        data.addReference( "myRef2", Reference.from( folderRefContent2.getId().toString() ) );
        data.addReference( "myRef3", Reference.from( siteRefContent1.getId().toString() ) );
        data.addReference( "refToMyself", Reference.from( "contentId" ) );

        final Content content = createContent( "contentId", data, ContentTypeName.site() );

        Mockito.when( contentService.getByIds( Mockito.any() ) ).thenReturn(
            Contents.from( folderRefContent1, folderRefContent2, siteRefContent1 ) );
        Mockito.when( contentService.getById( content.getId() ) ).thenReturn( content );

        final ContentIds result = resolver.resolve( content.getId() );

        Assert.assertEquals( result.getSize(), 3 );
        Assert.assertTrue( result.contains( folderRefContent1.getId() ) );
        Assert.assertTrue( result.contains( folderRefContent2.getId() ) );
        Assert.assertTrue( result.contains( siteRefContent1.getId() ) );

    }

    @Test
    public void resolve_outbound_with_missing_dependency()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();
        final Content folderRefContent1 = createContent( "folderRefContent1", data, ContentTypeName.folder() );
        final Content folderRefContent2 = createContent( "folderRefContent2", data, ContentTypeName.folder() );

        data.addReference( "myRef1", Reference.from( folderRefContent1.getId().toString() ) );
        data.addReference( "myRef2", Reference.from( folderRefContent2.getId().toString() ) );
        data.addReference( "myRef3", Reference.from( "some-id" ) );

        final Content content = createContent( "content", data, ContentTypeName.site() );

        Mockito.when( contentService.getByIds( Mockito.any() ) ).thenReturn( Contents.from( folderRefContent1, folderRefContent2 ) );
        Mockito.when( contentService.getById( content.getId() ) ).thenReturn( content );

        final ContentIds result = resolver.resolve( content.getId() );

        Assert.assertEquals( result.getSize(), 3 );
        Assert.assertTrue( result.contains( ContentId.from( "some-id" ) ) );
    }

    @Test
    public void resolve_outbound_from_xdata()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();

        final Content folderRefContent1 = createContent( "folderRefContent1", data, ContentTypeName.folder() );

        final Content folderRefContent2 = createContent( "folderRefContent2", data, ContentTypeName.folder() );

        final Content siteRefContent1 = createContent( "siteRefContent1", data, ContentTypeName.site() );

        data.addReference( "myRef1", Reference.from( folderRefContent1.getId().toString() ) );
        data.addReference( "myRef2", Reference.from( folderRefContent2.getId().toString() ) );
        data.addReference( "myRef3", Reference.from( siteRefContent1.getId().toString() ) );
        data.addReference( "refToMyself", Reference.from( "contentId" ) );

        final Content content = createContent( "contentId", new PropertyTree(), ContentTypeName.site(),
                                               ExtraDatas.create().add( new ExtraData( XDataName.from( "x-data" ), data ) ).build() );

        Mockito.when( contentService.getByIds( Mockito.any() ) ).thenReturn(
            Contents.from( folderRefContent1, folderRefContent2, siteRefContent1 ) );
        Mockito.when( contentService.getById( content.getId() ) ).thenReturn( content );

        final ContentIds result = resolver.resolve( content.getId() );

        Assert.assertEquals( result.getSize(), 3 );
        Assert.assertTrue( result.contains( folderRefContent1.getId() ) );
        Assert.assertTrue( result.contains( folderRefContent2.getId() ) );
        Assert.assertTrue( result.contains( siteRefContent1.getId() ) );
    }
}