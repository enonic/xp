module app.view {

    export class CloseAction extends api.ui.Action {

        constructor(itemViewPanel: api.app.view.ItemViewPanel<api.content.ContentSummary>, checkCanRemovePanel: boolean = true) {
            super("Close", "mod+f4");

            this.onExecuted(() => {
                itemViewPanel.close(checkCanRemovePanel);
            });
        }
    }
}
