module app.browse.action {

    import Action = api.ui.Action;

    export class ShowNewContentDialogAction extends Action {

        constructor(grid: ContentTreeGrid) {
            super("New", "mod+alt+n");
            this.setEnabled(true);
            this.onExecuted(() => {
                var contentSummaries: api.content.ContentSummary[]
                    = grid.getSelectedDataNodes().map((elem) => { return elem.getContentSummary(); });
                new ShowNewContentDialogEvent(contentSummaries.length > 0 ? contentSummaries[0] : null).fire();
            });
        }
    }
}
