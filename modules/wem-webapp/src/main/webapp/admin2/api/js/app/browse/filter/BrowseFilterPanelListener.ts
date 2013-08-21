module api_app_browse_filter {

    export interface BrowseFilterPanelListener extends api_event.Listener {

        onSearch?(values:any[]);

        onReset?();
    }

}