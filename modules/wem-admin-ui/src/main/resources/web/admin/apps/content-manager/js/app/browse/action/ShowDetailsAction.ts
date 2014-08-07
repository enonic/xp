module app.browse.action {

    import Action = api.ui.Action;

    export class ShowDetailsAction extends Action {

        constructor(grid: ContentTreeGrid) {
            super("DETAILS");
            this.setEnabled(false);
            this.onExecuted(() => {
                var contentSummaries: api.content.ContentSummary[]
                    = grid.getSelectedDataList().map((elem) => {
                    return elem.getContentSummary();
                });
                new ShowDetailsEvent(contentSummaries).fire();
            });
        }
    }
}
