module api_ui_toolbar {

    export class Toolbar extends api_dom.DivEl {

        ext;

        private components:any[] = [];

        constructor() {
            super("Toolbar", "toolbar");
            this.initExt();
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
            this.appendChild(button);
        }

        addActions(actions:api_ui.Action[]) {
            actions.forEach((action:api_ui.Action) => {
                this.addAction(action);
            });
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

    class ToolbarButton extends api_ui.AbstractButton {

        private action:api_ui.Action;

        constructor(action:api_ui.Action) {
            super("ToolbarButton", action.getLabel());
            this.action = action;
            this.getEl().addEventListener("click", (evt:Event) => {
                this.action.execute();
            });
            if (action.getIconClass()) {
                this.getEl().addClass(action.getIconClass());
            }
            this.setEnabled(action.isEnabled());

            action.addPropertyChangeListener((action:api_ui.Action) => {
                this.setEnabled(action.isEnabled());
            });
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
