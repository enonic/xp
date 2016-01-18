module app.browse {

    import FoldButton = api.ui.toolbar.FoldButton;

    export class MobileContentBrowseToolbar extends api.dom.DivEl implements api.ui.ActionContainer {

        private actions: api.ui.Action[] = [];

        constructor(actions: app.browse.action.MobileContentTreeGridActions) {
            super("toolbar");

            var foldButton = this.initFoldButton();

            this.addActions(actions.getAllActions(), foldButton);

            this.initEditButton(actions.EDIT_CONTENT);
        }

        private initFoldButton(): FoldButton {
            var fold = new FoldButton();
            fold.setLabel('More...');
            this.appendChild(fold);
            return fold;
        }

        private initEditButton(editAction: api.ui.Action) {
            var editButton = new api.ui.button.ActionButton(editAction);
            editButton.addClass("mobile-edit-action");
            this.appendChild(editButton);
        }

        private addActions(actions: api.ui.Action[], foldButton: FoldButton) {
            this.actions = this.actions.concat(actions);
            actions.forEach((action) => {

                var actionButton = new api.ui.button.ActionButton(action);
                var buttonWidth = actionButton.getEl().getWidthWithBorder();
                foldButton.push(actionButton, buttonWidth);
            });
        }

        getActions(): api.ui.Action[] {
            return this.actions;
        }

    }
}
