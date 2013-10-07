module api_ui_menu {

    export class MenuItem extends api_dom.LiEl {

        private action:api_ui.Action;

        constructor(action:api_ui.Action) {
            super("menu-item");
            this.action = action;
            this.getEl().setInnerHtml(this.action.getLabel());
            this.getEl().addEventListener("click", () => {
                if (action.isEnabled()) {
                    this.action.execute();
                }
            });
            this.setEnable(action.isEnabled());

            action.addPropertyChangeListener((action:api_ui.Action) => {
                this.setEnable(action.isEnabled());
            });
        }

        setEnable(value:boolean) {
            var el = this.getEl();
            el.setDisabled(!value);
            if (value) {
                el.removeClass("disabled");
            } else {
                el.addClass("disabled");
            }
        }
    }

}