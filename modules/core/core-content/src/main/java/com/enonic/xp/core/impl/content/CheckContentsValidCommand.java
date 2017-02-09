package com.enonic.xp.core.impl.content;

import java.util.stream.Collectors;

import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentIndexPath;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.FindContentByQueryParams;
import com.enonic.xp.content.FindContentByQueryResult;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.query.filter.IdFilter;
import com.enonic.xp.query.filter.ValueFilter;

public class CheckContentsValidCommand
    extends AbstractContentCommand
{

    private final ContentIds contentIds;

    private CheckContentsValidCommand( final Builder builder )
    {
        super( builder );
        contentIds = builder.contentIds;
    }

    public ContentIds execute()
    {
        if ( this.contentIds.getSize() == 0 )
        {
            return ContentIds.empty();
        }

        final ContentQuery query = ContentQuery.create().
            queryFilter( ValueFilter.create().
                fieldName( ContentPropertyNames.VALID ).
                addValue( ValueFactory.newBoolean( false ) ).
                build() ).
            queryFilter( IdFilter.create().
                fieldName( ContentIndexPath.ID.getPath() ).
                values( contentIds.asStrings() ).
                build() ).
            size( -1 ).
            build();

        final FindContentByQueryResult result = FindContentByQueryCommand.create().
            params( FindContentByQueryParams.create().
                contentQuery( query ).
                populateChildren( false ).
                build() ).
            contentTypeService( this.contentTypeService ).
            eventPublisher( this.eventPublisher ).
            nodeService( this.nodeService ).
            translator( this.translator ).
            build().
            execute();

        return ContentIds.from( result.getContents().stream().map( content -> content.getId() ).collect( Collectors.toList() ) );
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private ContentIds contentIds;

        private Builder()
        {
        }

        public Builder contentIds( final ContentIds val )
        {
            contentIds = val;
            return this;
        }

        public CheckContentsValidCommand build()
        {
            return new CheckContentsValidCommand( this );
        }
    }
}
