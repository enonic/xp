module app.browse.grid.actions {

    import Action = api.ui.Action;

    export class ShowPreviewAction extends Action {

        constructor(grid: ContentGridPanel2) {
            super("PREVIEW");
            this.setEnabled(false);
            this.onExecuted(() => {
                // TODO: Replace ContentSummary with ContentSummaryAndCompareStatus in future
                var contentSummaries: api.content.ContentSummary[]
                    = grid.getSelectedDataNodes().map((elem) => { return elem.getContentSummary(); });
                new ShowPreviewEvent(contentSummaries).fire();
            });
        }
    }
}
