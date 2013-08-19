module app_browse_filter {

    export class SchemaBrowseFilterPanel extends api_app_browse_filter.BrowseFilterPanel {


        constructor() {
            super([
                {
                    "name": "Type",
                    "displayName": "Type",
                    "terms": [
                        { "name": "Content Type", "key": 'contentType', "count": 6 },
                        { "name": "Relationship Type", "key": 'relationshipType', "count": 2 },
                        { "name": "Mixin", "key": 'mixin', "count": 5 }
                    ]
                },
                {
                    "name": "Module",
                    "displayName": "Module",
                    "terms": [
                        { "name": "system", "key": 'system', "count": 4 },
                        { "name": "demo", "key": 'demo', "count": 7 },
                        { "name": "B", "key": 'b', "count": 1 },
                        { "name": "C", "key": 'c', "count": 3 }
                    ]
                }
            ]);
            this.addListener({onSearch: (values:any[])=> {
                var params = app_browse.createLoadContentParams(values);
                api_remote_schema.RemoteSchemaService.schema_list(params, (response:api_remote_schema.ListResult) => {
                    if (this.isDirty()) {
                        new SchemaBrowseSearchEvent(params).fire();
                    } else {
                        new SchemaBrowseResetEvent().fire();
                    }
                    //TODO: update filter facets when they are implemented
                });

            }});
            this.addListener({onReset: ()=> {
                //TODO: reset filter facets when they are implemented
                new SchemaBrowseResetEvent().fire();
            }});
        }
    }
}
