package com.enonic.wem.api.content.query;

import com.enonic.wem.api.query.expr.QueryExpr;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;

public class ContentQuery
{
    public static final int DEFAULT_FETCH_SIZE = 10;

    private final QueryExpr queryExpr;

    private final ContentTypeNames contentTypeNames;

    private final int from;

    private final int size;

    public ContentQuery( final Builder builder )
    {
        this.queryExpr = builder.queryExpr;
        this.contentTypeNames = builder.contentTypeNamesBuilder.build();
        this.from = builder.from;
        this.size = builder.size;
    }

    public static Builder newContentQuery()
    {
        return new Builder();
    }

    public QueryExpr getQueryExpr()
    {
        return queryExpr;
    }

    public ContentTypeNames getContentTypes()
    {
        return contentTypeNames;
    }

    public int getFrom()
    {
        return from;
    }

    public int getSize()
    {
        return size;
    }

    public static class Builder
    {
        private QueryExpr queryExpr;

        private ContentTypeNames.Builder contentTypeNamesBuilder = new ContentTypeNames.Builder();

        private int from = 0;

        private int size = DEFAULT_FETCH_SIZE;


        public Builder queryExpr( final QueryExpr queryExpr )
        {
            this.queryExpr = queryExpr;
            return this;
        }

        public Builder addContentTypeName( final ContentTypeName contentTypeName )
        {
            this.contentTypeNamesBuilder.add( contentTypeName );
            return this;
        }


        public Builder addContentTypeNames( final ContentTypeNames contentTypeNames )
        {
            this.contentTypeNamesBuilder.addAll( contentTypeNames );
            return this;
        }

        public Builder from( final int from )
        {
            this.from = from;
            return this;
        }

        public Builder size( final int size )
        {
            this.size = size;
            return this;
        }

        public ContentQuery build()
        {
            return new ContentQuery( this );
        }
    }

}
