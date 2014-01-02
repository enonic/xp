module api.ui.toolbar {

    export class Toolbar extends api.dom.DivEl implements api.ui.ActionContainer {

        private components:any[] = [];

        private greedySpacerInsertPoint;

        private actions:api.ui.Action[] = [];

        constructor() {
            super("Toolbar", "toolbar");
        }

        afterRender() {
            super.afterRender();
        }

        addAction(action:api.ui.Action) {
            var button:ToolbarButton = this.addActionButton(action);
            this.actions.push(action);
            this.addElement(button);
        }

        addActions(actions:api.ui.Action[]) {
            actions.forEach((action:api.ui.Action) => {
                this.addAction(action);
            });
        }

        getActions():api.ui.Action[] {
            return this.actions;
        }

        addElement(element:api.dom.Element) {
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

        private addActionButton(action:api.ui.Action):api.ui.toolbar.ToolbarButton {
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

    export class ToolbarButton extends api.ui.ActionButton {

        constructor(action:api.ui.Action) {
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
