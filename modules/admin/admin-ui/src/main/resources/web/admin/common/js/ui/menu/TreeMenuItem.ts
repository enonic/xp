module api.ui.menu {

    export class TreeMenuItem extends api.dom.DdDtEl {
        private action: api.ui.Action;

        constructor(action: api.ui.Action, cls: string = "", expanded: boolean = false) {
            super(action.hasParentAction() ? "dd" : "dt");

            this.action = action;
            cls = this.getCls(action, cls, expanded);
            if (cls) {
                this.setClass(cls);
            }
            this.getEl().setInnerHtml(action.getLabel());
            this.onClicked((event: MouseEvent) => {
                if (action.isEnabled()) {
                    if (action.hasChildActions()) {
                        this.toggleExpand();
                    }
                    else {
                        action.execute();
                    }
                }
            });
            this.setEnabled(action.isEnabled());

            action.onPropertyChanged((changedAction: api.ui.Action) => {
                this.setEnabled(changedAction.isEnabled());
                this.setVisible(changedAction.isVisible());
            });
        }

        public toggleExpand() {
            this.toggleClass("expanded");
        }

        private getCls(action: api.ui.Action, cls: string = "", expanded: boolean = false): string {
            let fullCls = action.hasChildActions() ? "collapsible " : "";
            fullCls += expanded ? "expanded " : "";

            return fullCls + cls;
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
    }

}