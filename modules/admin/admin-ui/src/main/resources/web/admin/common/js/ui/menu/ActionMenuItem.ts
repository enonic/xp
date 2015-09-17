module api.ui.menu {

    export class ActionMenuItem extends api.dom.LiEl {

        private action: api.ui.Action;

        constructor(action: api.ui.Action) {

            super("action");
            this.action = action;

            this.getEl().setInnerHtml(this.action.getLabel());

            this.action.onPropertyChanged(() => {
                if (this.action.isEnabled()) {
                    this.show();
                }
                else if (!this.action.isEnabled()) {
                    this.hide();
                }
            });

            this.onClicked(() => {
                this.action.execute();
            });
        }
    }
}