module app.browse.grid.actions {

    import Action = api.ui.Action;

    export class OpenContentAction extends Action {

        constructor(grid: ContentGridPanel2) {
            // TODO: Enable shortcuts, when the old toolbar actions are removed.
            super("Open"/*, "mod+o"*/);
            this.setEnabled(false);
            this.onExecuted(() => {
                // TODO: Replace ContentSummary with ContentSummaryAndCompareStatus in future
                var contentSummaries: api.content.ContentSummary[]
                    = grid.getSelectedDataNodes().map((elem) => { return elem.getContentSummary(); });
                new ViewContentEvent(contentSummaries).fire();
            });
        }
    }
}
