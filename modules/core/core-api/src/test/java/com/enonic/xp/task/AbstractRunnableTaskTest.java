package com.enonic.xp.task;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.support.JsonTestHelper;

public abstract class AbstractRunnableTaskTest
{
    protected AuthenticationInfo authInfo;

    protected List<Content> contents;

    protected ContentService contentService;

    protected TaskService taskService;

    protected ProgressReporter progressReporter;

    protected ArgumentCaptor<String> contentQueryArgumentCaptor;

    protected JsonTestHelper jsonTestHelper;

    @BeforeEach
    void setUpAbstractRunnableTaskTest()
        throws Exception
    {
        this.authInfo = AuthenticationInfo.create().user( User.ANONYMOUS ).build();
        this.contents = new ArrayList<>( List.of(
            Content.create().id( ContentId.from( "id1" ) ).path( "/content/content1" ).name( "content1" ).displayName(
                "Content 1" ).build(),
            Content.create().id( ContentId.from( "id2" ) ).path( "/content/content2" ).name( "content2" ).displayName(
                "Content 2" ).build(),
            Content.create().id( ContentId.from( "id3" ) ).path( "/content/content3" ).name( "content3" ).displayName(
                "Content 3" ).build() ) );
        this.contentService = Mockito.mock( ContentService.class );
        this.taskService = Mockito.mock( TaskService.class );
        this.progressReporter = Mockito.mock( ProgressReporter.class );
        this.contentQueryArgumentCaptor = ArgumentCaptor.forClass( String.class );

        jsonTestHelper = new JsonTestHelper( this );
    }

    protected abstract AbstractRunnableTask createAndRunTask();
}
