module API_ui_toolbar {

    export class Toolbar extends API_ui.Component {

        ext;

        private components:any[] = [];

        constructor() {
            super("toolbar", "div");
            this.getEl().addClass("toolbar");
            this.initExt();
        }

        private initExt() {
            var htmlEl = this.getHTMLElement();
            this.ext = new Ext.Component({
                contentEl: htmlEl,
                region: 'north'
            });
        }

        addAction(action:API_action.Action) {
            var button:Button = this.doAddAction(action);
            this.appendChild(button);
        }

        addGreedySpacer() {
            var spacer = new ToolbarGreedySpacer();
            this.components.push(spacer);
        }

        private doAddAction(action:API_action.Action):Button {
            var button:API_ui_toolbar.Button = new API_ui_toolbar.Button(action);
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

    export class ToolbarGreedySpacer {

    }
}
