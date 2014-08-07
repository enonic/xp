module app.browse.action {

    import Action = api.ui.Action;

    export class DuplicateContentAction extends Action {

        constructor(grid: ContentTreeGrid) {
            super("Duplicate");
            this.setEnabled(false);
            this.onExecuted(() => {
                var contentSummaries: api.content.ContentSummary[]
                    = grid.getSelectedDataList().map((elem) => {
                    return elem.getContentSummary();
                });
                new DuplicateContentEvent(contentSummaries).fire();
            });
        }
    }
}
