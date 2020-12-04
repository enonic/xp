package com.enonic.xp.lib.content;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentInheritType;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ResetContentInheritParams;
import com.enonic.xp.content.SyncContentService;

public class ResetInheritanceHandlerTest
    extends BaseContentHandlerTest
{
    private SyncContentService syncContentService;

    private ArgumentCaptor<ResetContentInheritParams> captor;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();

        this.syncContentService = Mockito.mock( SyncContentService.class );
        addService( SyncContentService.class, this.syncContentService );

        this.captor = ArgumentCaptor.forClass( ResetContentInheritParams.class );
    }

    @Test
    public void testExample()
    {
        final Content content = Mockito.mock( Content.class );

        Mockito.when( content.getId() ).thenReturn( ContentId.from( "mycontent-id" ) );
        Mockito.when( content.getPath() ).thenReturn( ContentPath.from( "/a/b/mycontent" ) );

        Mockito.when( contentService.getByPath( Mockito.any( ContentPath.class ) ) ).thenReturn( content );
        Mockito.when( contentService.getById( Mockito.any( ContentId.class ) ) ).thenReturn( content );

        runScript( "/lib/xp/examples/content/resetInheritance.js" );

        Mockito.verify( this.syncContentService, Mockito.times( 2 ) ).resetInheritance( captor.capture() );

        final ResetContentInheritParams idParams = this.captor.getAllValues().get( 0 );

        Assert.assertEquals( "mycontent-id", idParams.getContentId().toString() );
        Assert.assertEquals( "child1", idParams.getProjectName().toString() );
        Assert.assertTrue( idParams.getInherit().contains( ContentInheritType.NAME ) );
        Assert.assertTrue( idParams.getInherit().contains( ContentInheritType.CONTENT ) );

        final ResetContentInheritParams pathParams = this.captor.getAllValues().get( 1 );

        Assert.assertEquals( "mycontent-id", pathParams.getContentId().toString() );
        Assert.assertEquals( "child2", pathParams.getProjectName().toString() );
        Assert.assertTrue( pathParams.getInherit().contains( ContentInheritType.SORT ) );
    }
}
