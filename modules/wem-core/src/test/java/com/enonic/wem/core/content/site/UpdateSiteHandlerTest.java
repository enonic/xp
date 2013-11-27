package com.enonic.wem.core.content.site;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.collect.ImmutableSet;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.GetContents;
import com.enonic.wem.api.command.content.UpdateContent;
import com.enonic.wem.api.command.content.site.UpdateSite;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.content.site.SiteEditor;
import com.enonic.wem.api.content.site.SiteNotFoundException;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;

import static junit.framework.Assert.assertEquals;

public class UpdateSiteHandlerTest
    extends AbstractCommandHandlerTest
{
    private UpdateSiteHandler handler;

    @Before
    public void before()
        throws Exception
    {
        super.client = Mockito.mock( Client.class );
        super.initialize();

        handler = new UpdateSiteHandler();
        handler.setContext( this.context );
    }

    @Test
    public void update_site_modified_site()
        throws Exception
    {
        Site.Builder siteBuilder = new Site.Builder();
        final Site originalSite = siteBuilder.build();

        siteBuilder = new Site.Builder();
        final Site editedSite = siteBuilder.build();

        final Content.Builder contentBuilder = Content.newContent().site( originalSite ).id( ContentId.from( "1" ) );
        final Content content = contentBuilder.build();

        Mockito.when( client.execute( Mockito.any( GetContents.class ) ) ).thenReturn( Contents.from( content ) );

        final Site.EditBuilder editBuilder = new Site.EditBuilder( originalSite )
        {
            @Override
            public boolean isChanges()
            {
                return true;
            }

            @Override
            public Site build()
            {
                return editedSite;
            }
        };

        final SiteEditor editor = new SiteEditor()
        {
            @Override
            public Site.EditBuilder edit( final Site toBeEdited )
            {
                return editBuilder;
            }
        };

        final UpdateSite command = new UpdateSite().content( content.getId() ).editor( editor );

        Mockito.when( client.execute( Mockito.isA( UpdateContent.class ) ) ).thenAnswer( new Answer<UpdateContent>() {
            public UpdateContent answer(InvocationOnMock invocation) throws Throwable {
                UpdateContent updateContent = ( UpdateContent ) invocation.getArguments()[0];
                final Content.EditBuilder builder = updateContent.getEditor().edit( content );

                final Content content = builder.build();
                assertEquals( editedSite, content.getSite() );

                return updateContent;
            }
        } );

        assertEquals( originalSite, content.getSite() );

        // exercise
        this.handler.setCommand( command );
        handler.handle();

        assertEquals( editedSite, command.getResult().getSite() );
    }

    @Test
    public void update_site_unmodified_site()
        throws Exception
    {
        Site.Builder siteBuilder = new Site.Builder();
        final Site originalSite = siteBuilder.build();

        final Content.Builder contentBuilder = Content.newContent().site( originalSite ).id( ContentId.from( "1" ) );
        final Content content = contentBuilder.build();

        Mockito.when( client.execute( Mockito.any( GetContents.class ) ) ).thenReturn( Contents.from( content ) );

        final Site.EditBuilder editBuilder = new Site.EditBuilder( originalSite )
        {
            @Override
            public boolean isChanges()
            {
                return false;
            }
        };

        final SiteEditor editor = new SiteEditor()
        {
            @Override
            public Site.EditBuilder edit( final Site toBeEdited )
            {
                return editBuilder;
            }
        };

        final UpdateSite command = new UpdateSite().content( content.getId() ).editor( editor );

        assertEquals( originalSite, content.getSite() );

        // exercise
        this.handler.setCommand( command );
        handler.handle();

        Mockito.verify( client, Mockito.times( 0 ) ).execute( Mockito.isA( UpdateContent.class ) );
    }

    @Test(expected = ContentNotFoundException.class)
    public void update_site_throws_ContentNotFoundException()
        throws Exception
    {
        final UpdateSite command = new UpdateSite().content( ContentId.from( "1" ) );

        final ImmutableSet<Content> set = ImmutableSet.of();
        final Contents contents = Contents.from( set );

        Mockito.when( client.execute( Mockito.isA( GetContents.class ) ) ).thenReturn( contents );

        // exercise
        this.handler.setCommand( command );
        handler.handle();
    }

    @Test(expected = SiteNotFoundException.class)
    public void update_site_throws_SiteNotFoundException()
        throws Exception
    {
        final UpdateSite command = new UpdateSite().content( ContentId.from( "1" ) );

        final Content.Builder contentBuilder = Content.newContent();
        final Content persistedContent = contentBuilder.build();

        Mockito.when( client.execute( Mockito.any( GetContents.class ) ) ).thenReturn( Contents.from( persistedContent ) );

        // exercise
        this.handler.setCommand( command );
        handler.handle();
    }
}