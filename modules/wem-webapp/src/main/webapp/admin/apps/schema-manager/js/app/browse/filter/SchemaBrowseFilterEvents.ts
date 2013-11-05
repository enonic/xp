module app_browse_filter {

    export class SchemaBrowseSearchEvent extends api_event.Event {

        private model:api_schema.SchemaJson[];

        constructor(model?:api_schema.SchemaJson[]) {
            super('schemaBrowseSearch');
            this.model = model;
        }

        getJsonModels():api_schema.SchemaJson[] {
            return this.model;
        }

        static on(handler:(event:SchemaBrowseSearchEvent)=>void) {
            api_event.onEvent('schemaBrowseSearch', handler);
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