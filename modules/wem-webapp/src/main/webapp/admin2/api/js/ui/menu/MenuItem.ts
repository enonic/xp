module api_ui_menu {

    export class MenuItem extends api_ui.LiEl {

        private action:api_action.Action;

        constructor(action:api_action.Action) {
            super("menu-item");
            this.action = action;
            this.getEl().setInnerHtml(this.action.getLabel());
            this.getEl().addEventListener("click", () => {
                if (action.isEnabled()) {
                    this.action.execute();
                }
            });
            this.setEnable(action.isEnabled());

            action.addPropertyChangeListener((action:api_action.Action) => {
                this.setEnable(action.isEnabled());
            });
        }

        setEnable(value:bool) {
            var el = this.getEl();
            el.setDisabled(!value);
            if (value) {
                el.removeClass("context-menu-item-disabled");
            } else {
                el.addClass("context-menu-item-disabled");
            }
        }
    }

}