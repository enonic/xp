module app_view {

    export interface ContentItemViewPanelParams {
        showPreviewAction:api_ui.Action;
        showDetailsAction:api_ui.Action;
    }

    export class ContentItemViewPanel extends api_app_view.ItemViewPanel<api_content.ContentSummary> {

        private statisticsPanel:api_app_view.ItemStatisticsPanel<api_content.ContentSummary>;

        private statisticsPanelIndex:number;

        private previewPanel;

        private previewMode:boolean;

        private previewPanelIndex:number;

        private deckPanel:api_ui.DeckPanel;

        private editAction:api_ui.Action;

        private deleteAction:api_ui.Action;

        private closeAction:api_ui.Action;

        constructor(params:ContentItemViewPanelParams) {

            this.deckPanel = new api_ui.DeckPanel();

            this.editAction = new EditContentAction(this);
            this.deleteAction = new DeleteContentAction(this);
            this.closeAction = new CloseContentAction(this, true);

            var toolbar = new ContentItemViewToolbar({
                editAction: this.editAction,
                deleteAction: this.deleteAction,
                closeAction: this.closeAction,
                showPreviewAction: params.showPreviewAction,
                showDetailsAction: params.showDetailsAction
            });

            super(toolbar, this.deckPanel);

            this.statisticsPanel = new ContentItemStatisticsPanel({
                editAction: this.editAction,
                deleteAction: this.deleteAction
            });
            this.previewPanel = new app_browse.ContentItemPreviewPanel();

            this.statisticsPanelIndex = this.deckPanel.addPanel(this.statisticsPanel);
            this.previewPanelIndex = this.deckPanel.addPanel(this.previewPanel);

            this.showPreview(false);

            app_browse.ShowPreviewEvent.on((event) => {
                this.showPreview(true);
            });

            app_browse.ShowDetailsEvent.on((event) => {
                this.showPreview(false);
            });
        }

        setItem(item:api_app_view.ViewItem<api_content.ContentSummary>) {
            super.setItem(item);
            this.statisticsPanel.setItem(item);
            this.previewPanel.setItem(item);
        }


        public showPreview(enabled:boolean) {
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
