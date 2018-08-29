package com.enonic.xp.lib.content;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentVersionId;
import com.enonic.xp.content.SetActiveContentVersionResult;

public class SetActiveVersionHandlerTest
    extends BaseContentHandlerTest
{
    @Test
    public void testExample()
    {
        final ContentId contentId = ContentId.from( "contentId" );
        final ContentVersionId contentVersionId = ContentVersionId.from("90398ddd1b22db08d6a0f9f0d1629a5f4c4fe41d");
        final SetActiveContentVersionResult result = new SetActiveContentVersionResult(contentId, contentVersionId);
        Mockito.when( this.contentService.setActiveContentVersion( Mockito.any(), Mockito.any() ) ).
            thenReturn( result);

        final Content content = Content.create().
            id( contentId).
            parentPath( ContentPath.from( "/path/to" ) ).
            name( "mycontent" ).
            build();
        Mockito.when( this.contentService.getByPath( Mockito.any() ) ).thenReturn( content );

        runScript( "/site/lib/xp/examples/content/setActiveVersion.js" );
    }
}
