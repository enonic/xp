module api.ui.menu {

    export class TreeMenuItem extends api.dom.DdDtEl {

        private action: api.ui.Action;

        constructor(action: api.ui.Action, cls: string = "", expanded: boolean = false) {
            super(this.getTag(action), this.getCls(action, cls, expanded));
            this.action = action;
            this.getEl().setInnerHtml(this.action.getLabel());
            this.onClicked((event: MouseEvent) => {
                if (action.isEnabled()) {
                    if (action.hasChildActions()) {
                        this.toggleExpand();
                    }
                    else {
                        this.action.execute();
                    }
                }
            });
            this.setEnabled(action.isEnabled());

            action.onPropertyChanged((action: api.ui.Action) => {
                this.setEnabled(action.isEnabled());
                this.setVisible(action.isVisible());
            });
        }

        public toggleExpand() {
            this.toggleClass("expanded");
        }

        private getTag(action: api.ui.Action): string {
            return action.hasParentAction() ? "dd" : "dt";
        }

        private getCls(action: api.ui.Action, cls: string = "", expanded: boolean = false): string {
            var fullCls = action.hasChildActions() ? "collapsible " : "";
            fullCls += expanded ? "expanded " : "";

            return fullCls + cls;
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