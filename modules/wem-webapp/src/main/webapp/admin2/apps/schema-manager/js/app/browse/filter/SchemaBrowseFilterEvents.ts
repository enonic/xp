module app_browse_filter {

    export class SchemaBrowseSearchEvent extends api_event.Event {

        filterParams;

        constructor(filterParams?) {
            super('schemaBrowseSearch');
            this.filterParams = filterParams;
        }

        static on(handler:(event:SchemaBrowseSearchEvent)=>void) {
            api_event.onEvent('schemaBrowseSearch', handler);
        }

        getFilterParams() {
            return this.filterParams;
        }
    }

    export class SchemaBrowseResetEvent extends api_event.Event {

        constructor() {
            super('schemaBrowseReset');
        }

        static on(handler:(event:SchemaBrowseResetEvent)=>void) {
            api_event.onEvent('schemaBrowseReset', handler);
        }
    }

}