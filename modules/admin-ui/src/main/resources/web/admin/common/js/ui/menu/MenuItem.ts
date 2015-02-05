module api.ui.menu {

    export class MenuItem extends api.dom.LiEl {

        private action:api.ui.Action;

        constructor(action:api.ui.Action) {
            super("menu-item");
            this.action = action;
            this.getEl().setInnerHtml(this.action.getLabel());
            this.onClicked((event: MouseEvent) => {
                if (action.isEnabled()) {
                    this.action.execute();
                }
            });
            this.setEnabled(action.isEnabled());

            action.onPropertyChanged((action: api.ui.Action) => {
                this.setEnabled(action.isEnabled());
                this.setVisible(action.isVisible());
            });
        }

        getAction(): api.ui.Action {
            return this.action;
        }

        setEnabled(value: boolean) {
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