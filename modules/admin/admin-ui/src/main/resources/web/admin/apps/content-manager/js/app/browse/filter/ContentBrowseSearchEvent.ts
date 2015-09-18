module app.browse.filter {

    export class ContentBrowseSearchEvent extends api.event.Event {

        private contentQueryResult: api.content.ContentQueryResult<any,any>;
        private contentQuery: api.content.query.ContentQuery;

        constructor(contentQueryResult: api.content.ContentQueryResult<any,any>, contentQuery?: api.content.query.ContentQuery) {
            super();
            this.contentQueryResult = contentQueryResult;
            this.contentQuery = contentQuery;
        }

        getContentQueryResult(): api.content.ContentQueryResult<any,any> {
            return this.contentQueryResult;
        }

        getContentQuery(): api.content.query.ContentQuery {
            return this.contentQuery;
        }

        static on(handler: (event: ContentBrowseSearchEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ContentBrowseSearchEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}