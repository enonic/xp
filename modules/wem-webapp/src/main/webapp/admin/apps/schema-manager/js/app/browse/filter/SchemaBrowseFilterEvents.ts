module app.browse.filter {

    export class SchemaBrowseSearchEvent extends api.event.Event {

        private model:api.schema.SchemaJson[];

        constructor(model?:api.schema.SchemaJson[]) {
            super('schemaBrowseSearch');
            this.model = model;
        }

        getJsonModels():api.schema.SchemaJson[] {
            return this.model;
        }

        static on(handler:(event:SchemaBrowseSearchEvent)=>void) {
            api.event.onEvent('schemaBrowseSearch', handler);
        }
    }

    export class SchemaBrowseResetEvent extends api.event.Event {

        constructor() {
            super('schemaBrowseReset');
        }

        static on(handler:(event:SchemaBrowseResetEvent)=>void) {
            api.event.onEvent('schemaBrowseReset', handler);
        }
    }

}