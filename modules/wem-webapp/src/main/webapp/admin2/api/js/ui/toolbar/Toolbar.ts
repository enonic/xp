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
            var button:Button = new Button(action);
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


    class Button extends API_ui.Component {

        private action:API_action.Action;

        constructor(action:API_action.Action) {
            super("button", "button");
            this.action = action;
            this.getEl().setInnerHtml(this.action.getLabel());
            this.getEl().addEventListener("click", () => {
                this.action.execute();
            });
            this.setEnable(action.isEnabled());

            action.addPropertyChangeListener((action:API_action.Action) => {
                this.setEnable(action.isEnabled());
            });
        }

        setEnable(value:bool) {
            this.getEl().setDisabled(!value);
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
