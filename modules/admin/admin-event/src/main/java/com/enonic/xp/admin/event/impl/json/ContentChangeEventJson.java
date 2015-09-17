package com.enonic.xp.admin.event.impl.json;

import com.enonic.xp.content.ContentChangeEvent;

public final class ContentChangeEventJson
    implements EventJson
{
    private ContentServerEventItemJson[] changes;

    public ContentChangeEventJson( final ContentChangeEvent event )
    {
        this.changes = event.getChanges().stream().map( ContentServerEventItemJson::new ).toArray( ContentServerEventItemJson[]::new );
    }

    public ContentServerEventItemJson[] getChanges()
    {
        return this.changes;
    }

    private final class ContentServerEventItemJson
    {
        private final String type;

        private final String[] contentPaths;

        private ContentServerEventItemJson( final ContentChangeEvent.ContentChange contentChange )
        {
            this.type = contentChange.getType().id();
            this.contentPaths = contentChange.getContentPaths().stream().
                map( ( contentPath ) -> contentPath.asAbsolute().toString() ).
                toArray( String[]::new );
        }

        public String getT()
        {
            return type;
        }

        public String[] getP()
        {
            return contentPaths;
        }
    }
}
