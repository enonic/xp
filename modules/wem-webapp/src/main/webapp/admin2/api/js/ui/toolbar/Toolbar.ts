module api_ui_toolbar {

    export class Toolbar extends api_dom.DivEl implements api_ui.ActionContainer {

        ext;

        private components:any[] = [];

        private actions:api_ui.Action[] = [];

        constructor() {
            super("Toolbar", "toolbar");
            this.initExt();
        }

        afterRender() {
            console.log("afterrender toolbar");
            super.afterRender();
        }

        private initExt() {
            var htmlEl = this.getHTMLElement();
            this.ext = new Ext.Component({
                contentEl: htmlEl,
                region: 'north'
            });
        }

        addAction(action:api_ui.Action) {
            var button:ToolbarButton = this.addActionButton(action);
            this.actions.push(action);
            this.appendChild(button);
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
            }
            this.appendChild(element);
        }

        addGreedySpacer() {
            var spacer = new ToolbarGreedySpacer();
            this.components.push(spacer);
        }

        private addActionButton(action:api_ui.Action):api_ui_toolbar.ToolbarButton {
            var button:ToolbarButton = new ToolbarButton(action);
            if (this.hasGreedySpacer()) {
                button.setFloatRight(true);
            }
            this.components.push(button);
            return button;
        }

        private hasGreedySpacer():bool {
            for (var i in this.components) {
                var comp = this.components[i];
                if (comp instanceof ToolbarGreedySpacer) {
                    return true;
                }
            }
            return false;
        }
    }

    class ToolbarButton extends api_ui.ActionButton {

        constructor(action:api_ui.Action) {
            super("ToolbarButton", action, true);
        }

        setFloatRight(value:bool) {
            if (value) {
                this.getEl().addClass('pull-right');
            }
        }
    }

    class ToolbarGreedySpacer {
        constructor() {
        }
    }

}
