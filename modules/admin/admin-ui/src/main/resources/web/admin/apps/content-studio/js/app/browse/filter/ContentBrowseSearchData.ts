import '../../../api.ts';

export class ContentBrowseSearchData {

    private contentQueryResult: api.content.resource.result.ContentQueryResult<any,any>;
    private contentQuery: api.content.query.ContentQuery;

    constructor(contentQueryResult: api.content.resource.result.ContentQueryResult<any,any>,
                contentQuery?: api.content.query.ContentQuery) {

        this.contentQueryResult = contentQueryResult;
        this.contentQuery = contentQuery;
    }

    getContentQueryResult(): api.content.resource.result.ContentQueryResult<any,any> {
        return this.contentQueryResult;
    }

    getContentQuery(): api.content.query.ContentQuery {
        return this.contentQuery;
    }
}
