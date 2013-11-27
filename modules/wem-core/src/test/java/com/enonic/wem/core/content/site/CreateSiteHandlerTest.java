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
import com.enonic.wem.api.command.content.site.CreateSite;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class CreateSiteHandlerTest
    extends AbstractCommandHandlerTest
{
    private CreateSiteHandler handler;

    @Before
    public void before()
        throws Exception
    {
        super.client = Mockito.mock( Client.class );
        super.initialize();

        handler = new CreateSiteHandler();
        handler.setContext( this.context );
    }

    @Test
    public void create_site()
        throws Exception
    {
        Content.Builder contentBuilder = Content.newContent().id( ContentId.from( "1" ) );
        final Content content = contentBuilder.build();

        final Site.Builder siteBuilder = new Site.Builder();
        final Site newSite = siteBuilder.build();

        contentBuilder = Content.newContent().site( newSite ).id( ContentId.from( "2" ) );
        final Content editedContent = contentBuilder.build();

        Mockito.when( client.execute( Mockito.any( GetContents.class ) ) ).thenReturn( Contents.from( content ) );

        final CreateSite command = new CreateSite().content( content.getId() ).template( SiteTemplateKey.from( "Intranet-1.0.0" ) );

        Mockito.when( client.execute( Mockito.isA( CreateSite.class ) ) ).thenAnswer( new Answer<CreateSite>()
        {
            public CreateSite answer( InvocationOnMock invocation )
                throws Throwable
            {
                CreateSite createSite = (CreateSite) invocation.getArguments()[0];
                createSite.setResult( editedContent );

                return createSite;
            }
        } );

        Mockito.when( client.execute( Mockito.isA( UpdateContent.class ) ) ).thenAnswer( new Answer<UpdateContent>()
        {
            public UpdateContent answer( InvocationOnMock invocation )
                throws Throwable
            {
                UpdateContent updateContent = (UpdateContent) invocation.getArguments()[0];
                final Content.EditBuilder builder = updateContent.getEditor().edit( content );

                final Content content = builder.build();
                assertEquals( newSite, content.getSite() );

                return updateContent;
            }
        } );

        assertNull( content.getSite() );

        // exercise
        this.handler.setCommand( command );
        handler.handle();

        assertEquals( newSite, command.getResult().getSite() );

        Mockito.verify( client, Mockito.times( 1 ) ).execute( Mockito.isA( CreateSite.class ) );
    }

    @Test(expected = ContentNotFoundException.class)
    public void create_site_throws_ContentNotFoundException()
        throws Exception
    {
        final CreateSite command = new CreateSite().content( ContentId.from( "1" ) );

        final ImmutableSet<Content> set = ImmutableSet.of();
        final Contents contents = Contents.from( set );

        Mockito.when( client.execute( Mockito.isA( GetContents.class ) ) ).thenReturn( contents );

        // exercise
        this.handler.setCommand( command );
        handler.handle();
    }
}