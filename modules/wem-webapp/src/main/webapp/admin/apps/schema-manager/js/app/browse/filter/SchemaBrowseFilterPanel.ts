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

            this.addListener({
                onSearch: (values:{[s:string] : string[]; })=> {
                    var params = this.createLoadContentParams(values);
                    //TODO: run find schemas request to get facets, and pass returned schemas to event
                    new SchemaBrowseSearchEvent().fire();

                },
                onReset: ()=> {
                    //TODO: reset filter facets when they are implemented
                    new SchemaBrowseResetEvent().fire();
                }});
        }

        createLoadContentParams(filterPanelValues:any) {
            var params:any = {types: [], modules: []};
            var paramTypes = params.types;
            var paramModules = params.modules;
            var typeFilter = filterPanelValues.Type;
            var moduleFilter = filterPanelValues.Module;
            if (typeFilter) {
                if (typeFilter.some(function (item) {
                    return item == 'Relationship Type'
                })) {
                    paramTypes.push('RELATIONSHIP_TYPE');
                }
                if (typeFilter.some(function (item) {
                    return item == 'Content Type'
                })) {
                    paramTypes.push('CONTENT_TYPE');
                }
                if (typeFilter.some(function (item) {
                    return item == 'Mixin'
                })) {
                    paramTypes.push('MIXIN');
                }
            }
            if (moduleFilter) {
                moduleFilter.forEach(function (moduleName) {
                    paramModules.push(moduleName);
                });
            }
            params.search = filterPanelValues.query;
            return params;
        }
    }
}
