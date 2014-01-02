module app.view {

    export interface ContentItemViewPanelParams {
        showPreviewAction:api.ui.Action;
        showDetailsAction:api.ui.Action;
    }

    export class ContentItemViewPanel extends api.app.view.ItemViewPanel<api.content.ContentSummary> {

        private statisticsPanel:api.app.view.ItemStatisticsPanel<api.content.ContentSummary>;

        private statisticsPanelIndex:number;

        private previewPanel;

        private previewMode:boolean;

        private previewPanelIndex:number;

        private deckPanel:api.ui.DeckPanel;

        private editAction:api.ui.Action;

        private deleteAction:api.ui.Action;

        private closeAction:api.ui.Action;

        constructor(params:ContentItemViewPanelParams) {

            this.deckPanel = new api.ui.DeckPanel();

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
            this.previewPanel = new app.browse.ContentItemPreviewPanel();

            this.statisticsPanelIndex = this.deckPanel.addPanel(this.statisticsPanel);
            this.previewPanelIndex = this.deckPanel.addPanel(this.previewPanel);

            this.showPreview(false);

            app.browse.ShowPreviewEvent.on((event) => {
                this.showPreview(true);
            });

            app.browse.ShowDetailsEvent.on((event) => {
                this.showPreview(false);
            });
        }

        setItem(item:api.app.view.ViewItem<api.content.ContentSummary>) {
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
