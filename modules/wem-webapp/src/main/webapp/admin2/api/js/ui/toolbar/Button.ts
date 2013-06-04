module API_ui_toolbar {

    export class Button extends API_ui.Component {

        private action:API_action.Action;

        constructor(action:API_action.Action) {
            super("button", "button");
            this.action = action;
            this.getEl().setInnerHtml(this.action.getLabel());
            this.getEl().addEventListener("click", () => {
                this.action.execute();
            });
            this.setEnable(action.isEnabled());

            action.addPropertyChangeListener((action:API_action.Action) => {
                this.setEnable(action.isEnabled());
            });
        }

        setEnable(value:bool) {
            this.getEl().setDisabled(!value);
        }

        setFloatRight(value:bool) {
            if (value) {
                this.getEl().addClass('pull-right');
            }
        }
    }
}
