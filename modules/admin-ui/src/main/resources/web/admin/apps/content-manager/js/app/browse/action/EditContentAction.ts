module app.browse.action {

    import Action = api.ui.Action;

    export class EditContentAction extends Action {

        constructor(grid: ContentTreeGrid) {
            super("Edit", "f4");
            this.setEnabled(false);
            this.onExecuted(() => {
                var contentSummaries: api.content.ContentSummary[]
                    = grid.getSelectedDataList().map((elem) => {
                    return elem.getContentSummary();
                });
                new EditContentEvent(contentSummaries).fire();
            });
        }
    }
}
