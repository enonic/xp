module app.browse.grid.actions {

    import Action = api.ui.Action;

    export class DuplicateContentAction extends Action {

        constructor(grid: ContentGridPanel2) {
            super("Duplicate");
            this.setEnabled(false);
            this.onExecuted(() => {
                // TODO: Replace ContentSummary with ContentSummaryAndCompareStatus in future
                var contentSummaries: api.content.ContentSummary[]
                    = grid.getSelectedDataNodes().map((elem) => { return elem.getContentSummary(); });
                new DuplicateContentEvent(contentSummaries).fire();
            });
        }
    }
}
