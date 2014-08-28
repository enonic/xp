package com.enonic.wem.core.content;

import java.util.Collection;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentVersion;
import com.enonic.wem.api.content.ContentVersions;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;

public class ContentVersionsFactory
{
    private final ContentNodeTranslator translator;

    public ContentVersionsFactory( final ContentNodeTranslator translator )
    {
        this.translator = translator;
    }

    public ContentVersions create( final EntityId entityId, final Collection<Node> nodes )
    {
        final ContentVersions.Builder contentVersionsBuilder = ContentVersions.create().
            contentId( ContentId.from( entityId ) );

        for ( final Node node : nodes )
        {
            final ContentVersion contentVersion = doCreateContentVersion( node );

            contentVersionsBuilder.add( contentVersion );
        }

        return contentVersionsBuilder.build();
    }

    public ContentVersion create( final Node node )
    {
        return doCreateContentVersion( node );
    }

    private ContentVersion doCreateContentVersion( final Node node )
    {
        final Content content = translator.fromNode( node );

        return ContentVersion.create().
            comment( "Dummy comment" ).
            displayName( content.getDisplayName() ).
            modified( content.getModifiedTime() ).
            modifier( content.getModifier() ).
            build();
    }


}
