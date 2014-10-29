module app.browse.filter {

    import PrincipalQuery = api.security.query.PrincipalQuery;
    import AggregationGroupView = api.aggregation.AggregationGroupView;

    //import RefreshEvent = api.app.browse.filter.RefreshEvent;
    //import SearchEvent = api.app.browse.filter.SearchEvent;


    export class PrincipalBrowseFilterPanel extends api.app.browse.filter.BrowseFilterPanel {

        static PRINCIPAL_TYPE_AGGREGATION_NAME: string = "type";
        static LAST_MODIFIED_AGGREGATION_NAME: string = "lastModified";
        static PRINCIPAL_TYPE_AGGREGATION_DISPLAY_NAME: string = "Type";
        static LAST_MODIFIED_AGGREGATION_DISPLAY_NAME: string = "Last Modified";
        static USERSTORE_AGGREGATION_NAME: string = "userStore";
        static USERSTORE_AGGREGATION_DISPLAY_NAME: string = "Userstore";
        private principalFilterPanel: app.browse.filter.PrincipalBrowseFilterPanel;

        constructor() {

            var contentTypeAggregation: api.aggregation.PrincipalAggregationGroupView = new api.aggregation.PrincipalAggregationGroupView(
                PrincipalBrowseFilterPanel.PRINCIPAL_TYPE_AGGREGATION_NAME,
                PrincipalBrowseFilterPanel.PRINCIPAL_TYPE_AGGREGATION_DISPLAY_NAME);
            var userStoreAggregation: AggregationGroupView = new AggregationGroupView(
                PrincipalBrowseFilterPanel.USERSTORE_AGGREGATION_NAME,
                PrincipalBrowseFilterPanel.USERSTORE_AGGREGATION_DISPLAY_NAME);

            var lastModifiedAggregation: AggregationGroupView = new AggregationGroupView(
                PrincipalBrowseFilterPanel.LAST_MODIFIED_AGGREGATION_NAME,
                PrincipalBrowseFilterPanel.LAST_MODIFIED_AGGREGATION_DISPLAY_NAME);

            super(null, [contentTypeAggregation, userStoreAggregation, lastModifiedAggregation]);


            // this.initAggregationGroupView([contentTypeAggregation,userStoreAggregation, lastModifiedAggregation]);

            this.onReset(()=> {
                // this.resetFacets();
            });

            // this.onRefresh(this.searchFacets);

            //  this.onSearch(this.searchFacets);
        }


    }
}
