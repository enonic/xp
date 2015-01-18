module app.browse.filter {

    import AggregationGroupView = api.aggregation.AggregationGroupView;
    import SearchInputValues = api.query.SearchInputValues;
    import Principal = api.security.Principal;
    import FindPrincipalsRequest = api.security.FindPrincipalsRequest;
    import PrincipalType = api.security.PrincipalType;

    export class PrincipalBrowseFilterPanel extends api.app.browse.filter.BrowseFilterPanel {


        constructor() {

            super(null);

            this.onReset(()=> {
                this.resetFacets();
            });

            this.onRefresh(this.searchFacets);

            this.onSearch(this.searchFacets);
        }

        private resetFacets(supressEvent?: boolean) {

            if (!supressEvent) { // then fire usual reset event with content grid reloading
                new PrincipalBrowseResetEvent().fire();
            }
        }

        private searchFacets(event: api.app.browse.filter.SearchEvent) {
            var searchText = event.getSearchInputValues().getTextSearchFieldValue();
            if (!searchText) {
                this.resetFacets(true);
                return;
            }
            new FindPrincipalsRequest().
                setAllowedTypes([PrincipalType.GROUP, PrincipalType.USER, PrincipalType.ROLE]).
                setSearchQuery(searchText).
                sendAndParse().then((principals: Principal[]) => {
                    new PrincipalBrowseSearchEvent(principals).fire();
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).done();
        }


    }
}
