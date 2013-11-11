module api_ui_toolbar {

    export class Toolbar extends api_dom.DivEl implements api_ui.ActionContainer {

        private components:any[] = [];

        private greedySpacerInsertPoint;

        private actions:api_ui.Action[] = [];

        constructor() {
            super("Toolbar", "toolbar");
        }

        afterRender() {
            console.log("afterrender toolbar");
            super.afterRender();
        }

        addAction(action:api_ui.Action) {
            var button:ToolbarButton = this.addActionButton(action);
            this.actions.push(action);
            this.addElement(button);
        }

        addActions(actions:api_ui.Action[]) {
            actions.forEach((action:api_ui.Action) => {
                this.addAction(action);
            });
        }

        getActions():api_ui.Action[] {
            return this.actions;
        }

        addElement(element:api_dom.Element) {
            if (this.hasGreedySpacer()) {
                element.getEl().addClass('pull-right');
                element.insertAfterEl(this.greedySpacerInsertPoint);
            } else {
                this.appendChild(element);
            }
        }

        addGreedySpacer() {
            var spacer = new ToolbarGreedySpacer();
            this.components.push(spacer);
            this.greedySpacerInsertPoint = this.getLastChild();
        }

        private addActionButton(action:api_ui.Action):api_ui_toolbar.ToolbarButton {
            var button:ToolbarButton = new ToolbarButton(action);
            this.components.push(button);
            return button;
        }

        private hasGreedySpacer():boolean {
            return this.components.some((comp:any) => {
                if (comp instanceof ToolbarGreedySpacer) {
                    return true;
                }
                return false;
            });
        }

    }

    export class ToolbarButton extends api_ui.ActionButton {

        constructor(action:api_ui.Action) {
            super("ToolbarButton", action, true);
        }

        setFloatRight(value:boolean) {
            if (value) {
                this.getEl().addClass('pull-right');
            }
        }
    }

    export class ToolbarGreedySpacer {
        constructor() {
        }
    }

}
