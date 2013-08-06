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
            var searchAction = new api_app_browse_filter.FilterSearchAction();
            searchAction.addExecutionListener((action:api_app_browse_filter.FilterSearchAction)=> {
                var params = app_browse.createLoadContentParams(action.getFilterValues());
                api_remote.RemoteSchemaService.schema_list(params, (response) => {
                    if (response && response.success) {
                        if (this.isDirty()) {
                            new SchemaBrowseSearchEvent(params).fire();
                        } else {
                            new SchemaBrowseResetEvent().fire();
                        }
                        //TODO: update filter facets when they are implemented
                    }
                });

            });
            var resetAction = new api_app_browse_filter.FilterResetAction();
            resetAction.addExecutionListener((action:api_app_browse_filter.FilterResetAction)=> {
                //TODO: reset filter facets when they are implemented
                new SchemaBrowseResetEvent().fire();
            });
            this.setFilterSearchAction(searchAction);
            this.setFilterResetAction(resetAction);
        }
    }
}
