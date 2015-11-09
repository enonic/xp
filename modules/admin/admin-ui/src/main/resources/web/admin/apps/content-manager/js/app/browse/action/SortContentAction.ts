module app.browse.action {

    import Action = api.ui.Action;

    export class SortContentAction extends Action {

        constructor(grid: ContentTreeGrid) {
            super("Sort");
            this.setEnabled(false);
            this.onExecuted(() => {
                var contents: api.content.ContentSummaryAndCompareStatus[]
                    = grid.getSelectedDataList();
                new app.browse.SortContentEvent(contents).fire();
            });
        }
    }
}
