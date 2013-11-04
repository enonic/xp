module api_app_view {

    export interface ItemViewPanelListener<M> {

        onClosed?: (panel:ItemViewPanel<M>) =>  void;

    }

}