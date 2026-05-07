package com.enonic.xp.core.content;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.DuplicateContentParams;
import com.enonic.xp.content.DuplicateContentsResult;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.acl.AccessControlList;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ContentServiceImplTest_getOutboundDependencies
    extends AbstractContentServiceTest
{
    @Test
    void testThatOutboundDependenciesContainsVariant()
    {
        Content originalContent = this.contentService.create( CreateContentParams.create()
                                                                  .contentData( new PropertyTree() )
                                                                  .displayName( "My Content" )
                                                                  .parent( ContentPath.ROOT )
                                                                  .type( ContentTypeName.folder() )
                                                                  .permissions( AccessControlList.create().build() )
                                                                  .build() );

        DuplicateContentsResult result = contentService.duplicate( DuplicateContentParams.create()
                                                                       .contentId( originalContent.getId() )
                                                                       .includeChildren( false )
                                                                       .variant( true )
                                                                       .name( "Variant Name" )
                                                                       .parent( originalContent.getPath() )
                                                                       .build() );

        final ContentId variantContentId = result.getDuplicatedContents().first();

        // Try to find dependencies for variant content
        ContentIds outboundDependencies = this.contentService.getOutboundDependencies( variantContentId );

        assertTrue( outboundDependencies.contains( originalContent.getId() ) );
    }
}
