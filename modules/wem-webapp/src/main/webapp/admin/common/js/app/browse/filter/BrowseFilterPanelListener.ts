module api.app.browse.filter {

    export interface BrowseFilterPanelListener extends api.event.Listener {

        onSearch(searchInputValues: api.query.SearchInputValues);

        onReset();
    }

}