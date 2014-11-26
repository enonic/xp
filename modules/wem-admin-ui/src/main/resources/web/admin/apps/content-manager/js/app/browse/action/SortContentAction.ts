module app.browse.action {

    import Action = api.ui.Action;

    export class SortContentAction extends Action {

        constructor(grid: ContentTreeGrid) {
            super("Sort");
            this.setEnabled(false);
            this.onExecuted(() => {
                var contentSummaries: api.content.ContentSummary[]
                    = grid.getSelectedDataList().map((elem) => {
                    return elem.getContentSummary();
                });
                new app.browse.SortContentEvent(contentSummaries).fire();
            });
        }
    }
}
