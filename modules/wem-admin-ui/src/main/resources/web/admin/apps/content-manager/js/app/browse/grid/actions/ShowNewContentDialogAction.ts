module app.browse.grid.actions {

    import Action = api.ui.Action;

    export class ShowNewContentDialogAction extends Action {

        constructor(grid: ContentGridPanel2) {
            // TODO: Enable shortcuts, when the old toolbar actions are removed.
            super("New"/*, "mod+alt+n"*/);
            this.setEnabled(true);
            this.onExecuted(() => {
                // TODO: Replace ContentSummary with ContentSummaryAndCompareStatus in future
                var contentSummaries: api.content.ContentSummary[]
                    = grid.getSelectedDataNodes().map((elem) => { return elem.getContentSummary(); });
                new ShowNewContentDialogEvent(contentSummaries.length > 0 ? contentSummaries[0] : null).fire();
            });
        }
    }
}
