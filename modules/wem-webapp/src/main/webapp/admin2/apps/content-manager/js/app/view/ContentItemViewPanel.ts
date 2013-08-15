module app_view {

    export class ContentItemViewPanel extends api_app_view.ItemViewPanel {

        private id:string;
        private editAction:api_ui.Action;
        private deleteAction:api_ui.Action;
        private closeAction:api_ui.Action;

        private statisticsPanel:api_app_view.ItemStatisticsPanel;
        private statisticsPanelIndex:number;
        private previewPanel;
        private previewMode:bool;
        private previewPanelIndex:number;
        private deckPanel:api_ui.DeckPanel;


        constructor(id:string) {

            this.id = id;
            this.editAction = new EditContentAction(this);
            this.deleteAction = new DeleteContentAction(this);
            this.closeAction = new CloseContentAction(this, true);

            var toolbar = new ContentItemViewToolbar({
                editAction: this.editAction,
                deleteAction: this.deleteAction,
                closeAction: this.closeAction
            });

            this.statisticsPanel = new ContentItemStatisticsPanel({
                editAction: this.editAction,
                deleteAction: this.deleteAction
            });
            this.previewPanel = new app_browse.ContentItemPreviewPanel();

            this.deckPanel = new api_ui.DeckPanel();

            this.statisticsPanelIndex = this.deckPanel.addPanel(this.statisticsPanel);
            this.previewPanelIndex = this.deckPanel.addPanel(this.previewPanel);

            this.showPreview(false);

            super(toolbar, this.deckPanel);

            app_browse.ShowPreviewEvent.on((event) => {
                this.showPreview(true);
            });

            app_browse.ShowDetailsEvent.on((event) => {
                this.showPreview(false);
            });
        }

        setItem(item:api_app_view.ViewItem) {
            super.setItem(item);
            this.statisticsPanel.setItem(item);
            this.previewPanel.setItem(item);
        }


        public showPreview(enabled:bool) {
            this.previewMode = enabled;
            // refresh the view
            if (enabled) {
                this.deckPanel.showPanel(this.previewPanelIndex);
            } else {
                this.deckPanel.showPanel(this.statisticsPanelIndex);
            }
        }

    }

}
