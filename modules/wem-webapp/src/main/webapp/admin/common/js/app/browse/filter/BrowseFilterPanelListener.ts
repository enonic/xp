module api.app.browse.filter {

    export interface BrowseFilterPanelListener extends api.event.Listener {

        onSearch(values:{[s:string] : string[]; });

        onReset();
    }

}