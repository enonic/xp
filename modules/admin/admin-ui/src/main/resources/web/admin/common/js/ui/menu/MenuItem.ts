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

            action.onPropertyChanged((changedAction: api.ui.Action) => {
                this.setEnabled(changedAction.isEnabled());
                this.setVisible(changedAction.isVisible());
            });
        }

        getAction(): api.ui.Action {
            return this.action;
        }

        setEnabled(value: boolean) {
            let el = this.getEl();
            el.setDisabled(!value);
            if (value) {
                el.removeClass("disabled");
            } else {
                el.addClass("disabled");
            }
        }

        isEnabled(): boolean {
            return this.action.isEnabled();
        }
    }

}
