module app_browse_filter {

    export class SchemaBrowseFilterPanel extends api_app_browse_filter.BrowseFilterPanel {

        constructor() {

            // dummy data
            var facet1Entries:api_facet.TermsFacetEntry[] = [];
            facet1Entries.push(new api_facet.TermsFacetEntry("ContentType", "Content Type", 6));
            facet1Entries.push(new api_facet.TermsFacetEntry("RelationshipType", "Relationship Type", 2));
            facet1Entries.push(new api_facet.TermsFacetEntry("Mixin", "Mixin", 5));
            var facet1 = new api_facet.TermsFacet("Type", "Type", facet1Entries);

            super([facet1]);

            this.addListener({onSearch: (values:{[s:string] : string[]; })=> {
                var params = app_browse.createLoadContentParams(values);
                api_remote_schema.RemoteSchemaService.schema_list(params, (response:api_remote_schema.ListResult) => {
                    if (this.hasFilterSet()) {
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
