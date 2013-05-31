module API_ui_toolbar {

    export class Toolbar extends API_ui.Component {

        ext;

        private buttons:Button[] = [];

        private element:HTMLElement;

        // TODO: create and expose HTML for tool bar

        constructor(actions:API_action.Action[]) {
            super("toolbar");
            for (var i in actions) {
                this.addAction(actions[i]);
            }

            this.initExt();
        }

        private initExt() {
            this.ext = new Ext.Component({
                contentEl: this.toHTMLElement(),
                region: 'north'
            });
        }

        toHTMLElement():HTMLElement {
            var divEl:HTMLElement = document.createElement("div");
            divEl.id = super.getId();
            for (var i in this.buttons) {
                divEl.appendChild(this.buttons[i].toHTMLElement());
            }
            return divEl;
        }

        add(action:API_action.Action) {
            var button:Button = this.addAction(action);
            this.element.appendChild(button.toHTMLElement())
        }

        private addAction(action:API_action.Action):Button {
            var button = new Button(action);
            this.buttons.push(button);
            return button;
        }
    }
}
