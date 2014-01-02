module api.app.view {

    export interface ItemViewPanelListener<M> {

        onClosed?: (panel:ItemViewPanel<M>) =>  void;

    }

}