module app.view {

    export class CloseAction extends api.ui.Action {

        constructor(itemViewPanel: api.app.view.ItemViewPanel<api.content.ContentSummaryAndCompareStatus>,
                    checkCanRemovePanel: boolean = true) {
            super("Close", "mod+alt+f4");

            this.onExecuted(() => {
                itemViewPanel.close(checkCanRemovePanel);
            });
        }
    }
}
