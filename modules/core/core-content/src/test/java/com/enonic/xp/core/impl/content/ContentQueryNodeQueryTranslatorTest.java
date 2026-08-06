package com.enonic.xp.core.impl.content;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.context.ContextAccessorSupport;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ContentQueryNodeQueryTranslatorTest
{
    @BeforeEach
    void setUp()
    {
        ContextAccessorSupport.getInstance()
            .set( ContextBuilder.create()
                      .repositoryId( "com.enonic.cms.default" )
                      .branch( ContentConstants.BRANCH_DRAFT )
                      .build() );
    }

    @AfterEach
    void tearDown()
    {
        ContextAccessorSupport.getInstance().remove();
    }

    @Test
    void translate_without_parent()
    {
        final NodeQuery nodeQuery = ContentQueryNodeQueryTranslator.translate( ContentQuery.create().build() ).build();

        assertNull( nodeQuery.getParent() );
    }

    @Test
    void translate_parent()
    {
        final NodeQuery nodeQuery =
            ContentQueryNodeQueryTranslator.translate( ContentQuery.create().parent( ContentPath.from( "/mysite/articles" ) ).build() )
                .build();

        assertEquals( new NodePath( "/content/mysite/articles" ), nodeQuery.getParent() );
    }

    @Test
    void translate_root_parent()
    {
        final NodeQuery nodeQuery =
            ContentQueryNodeQueryTranslator.translate( ContentQuery.create().parent( ContentPath.ROOT ).build() ).build();

        assertEquals( ContentConstants.CONTENT_ROOT_PATH, nodeQuery.getParent() );
    }

    @Test
    void translate_parent_of_content_root_from_context()
    {
        final NodePath contentRoot = new NodePath( "/mylayer" );

        ContextBuilder.from( ContextAccessorSupport.getInstance().get() )
            .attribute( ContentConstants.CONTENT_ROOT_PATH_ATTRIBUTE, contentRoot )
            .build()
            .runWith( () -> {
                final NodeQuery nodeQuery = ContentQueryNodeQueryTranslator.translate(
                    ContentQuery.create().parent( ContentPath.from( "/mysite" ) ).build() ).build();

                assertEquals( new NodePath( "/mylayer/mysite" ), nodeQuery.getParent() );
            } );
    }
}
