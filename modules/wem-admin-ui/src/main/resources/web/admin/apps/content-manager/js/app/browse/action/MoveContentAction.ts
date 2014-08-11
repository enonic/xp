module app.browse.action {

    import Action = api.ui.Action;

    export class MoveContentAction extends Action {

        constructor(grid: ContentTreeGrid) {
            super("Move");
            this.setEnabled(false);
            this.onExecuted(() => {
                var contentSummaries: api.content.ContentSummary[]
                    = grid.getSelectedDataList().map((elem) => {
                    return elem.getContentSummary();
                });
                new MoveContentEvent(contentSummaries).fire();
            });
        }
    }
}
