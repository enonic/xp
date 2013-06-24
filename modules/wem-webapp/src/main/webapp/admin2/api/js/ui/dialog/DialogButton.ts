module api_ui_dialog{

    export class DialogButton extends api_ui.AbstractButton {

        private action:api_ui.Action;

        constructor(action:api_ui.Action) {
            super("DialogButton", action.getLabel());
            this.getEl().addClass("DialogButton")
            this.action = action;
            this.getEl().addEventListener("click", () => {
                this.action.execute();
            });
            this.setEnabled(action.isEnabled());

            action.addPropertyChangeListener((action:api_ui.Action) => {
                this.setEnabled(action.isEnabled());
            });
        }
    }
}
