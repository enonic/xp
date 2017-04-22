package com.enonic.xp.core.impl.content;

import java.util.stream.Collectors;

import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentIndexPath;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.FindContentByQueryParams;
import com.enonic.xp.content.FindContentByQueryResult;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.LogicalExpr;
import com.enonic.xp.query.expr.NotExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.query.filter.IdFilter;

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

        final LogicalExpr contentIsUnnamed = LogicalExpr.
            or( new NotExpr( CompareExpr.
                    like( FieldExpr.from( ContentPropertyNames.DISPLAY_NAME ), ValueExpr.string( "*" ) ) ),
                CompareExpr.like( FieldExpr.from( ContentPropertyNames.NAME ), ValueExpr.string( ContentName.UNNAMED_PREFIX + "*" ) ) );

        final CompareExpr contentIsInvalid = CompareExpr.
            eq( FieldExpr.from( ContentPropertyNames.VALID ), ValueExpr.fromBoolean( false ) );

        final QueryExpr selectInvalidExpr = QueryExpr.from( LogicalExpr.
            or( contentIsUnnamed, contentIsInvalid ) );

        final ContentQuery query = ContentQuery.create().
            queryExpr( selectInvalidExpr ).
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
