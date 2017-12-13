package com.enonic.xp.lib.content;

import java.util.Arrays;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.util.BinaryReferences;

import static java.util.stream.Collectors.toList;

public final class RemoveAttachmentHandler
    implements ScriptBean
{
    private ContentService contentService;

    private String key;

    private String[] names;

    public void execute()
    {
        UpdateContentParams updateContent = new UpdateContentParams();
        if ( !this.key.startsWith( "/" ) )
        {
            updateContent.contentId( ContentId.from( this.key ) );
        }
        else
        {
            final Content contentByPath = this.contentService.getByPath( ContentPath.from( key ) );
            updateContent.contentId( contentByPath.getId() );
        }

        BinaryReferences binaryRefs = BinaryReferences.from( Arrays.stream( this.names ).map( BinaryReference::from ).collect( toList() ) );
        updateContent.removeAttachments( binaryRefs );
        contentService.update( updateContent );
    }

    public void setKey( final String key )
    {
        this.key = key;
    }

    public void setName( final String[] names )
    {
        this.names = names;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.contentService = context.getService( ContentService.class ).get();
    }

}
