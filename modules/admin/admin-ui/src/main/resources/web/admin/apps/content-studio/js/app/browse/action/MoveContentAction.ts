module app.browse.action {

    import Action = api.ui.Action;

    export class MoveContentAction extends Action {

        constructor(grid: ContentTreeGrid) {
            super("Move");
            this.setEnabled(false);
            this.onExecuted(() => {
                var contents: api.content.ContentSummaryAndCompareStatus[]
                    = grid.getSelectedDataList();
                new MoveContentEvent(contents).fire();
            });
        }
    }
}
