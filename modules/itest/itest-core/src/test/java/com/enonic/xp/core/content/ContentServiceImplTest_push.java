package com.enonic.xp.core.content;

import org.junit.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.content.PushContentsResult;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.content.WorkflowState;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.junit.Assert.*;

public class ContentServiceImplTest_push
    extends AbstractContentServiceTest
{
    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
    }

    @Test
    public void push_workflow_not_ready()
        throws Exception
    {
        //Creates a content
        final CreateContentParams createContentParams = CreateContentParams.create().
            contentData( new PropertyTree() ).
            displayName( "This is my content" ).
            name( "myContent" ).
            parent( ContentPath.ROOT ).
            type( ContentTypeName.folder() ).
            workflowInfo( WorkflowInfo.create().state( WorkflowState.REJECTED ).build() ).
            build();

        final Content content = this.contentService.create( createContentParams );

        final PushContentsResult result = this.contentService.push( PushContentParams.create().
            contentIds( ContentIds.from( content.getId() ) ).
            target( CTX_OTHER.getBranch() ).
            build() );

        assertEquals( 0, result.getDeletedContents().getSize() );
        assertEquals( 0, result.getFailedContents().getSize() );
        assertEquals( 0, result.getPushedContents().getSize() );
    }

}
