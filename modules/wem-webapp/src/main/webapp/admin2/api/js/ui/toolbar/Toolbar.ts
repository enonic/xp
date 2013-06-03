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

            this.element = this.createHTMLElement();
            this.initExt();
        }

        private initExt() {
            this.ext = new Ext.Component({
                contentEl: this.element,
                region: 'north'
            });
        }

        getHTMLElement():HTMLElement {
            return this.element;
        }

        private createHTMLElement():HTMLElement {
            var divEl:HTMLElement = document.createElement("div");
            divEl.id = super.getId();
            for (var i in this.buttons) {
                divEl.appendChild(this.buttons[i].getHTMLElement());
            }
            return divEl;
        }

        add(action:API_action.Action) {
            var button:Button = this.addAction(action);
            this.element.appendChild(button.getHTMLElement())
        }

        private addAction(action:API_action.Action):Button {
            var button = new Button(action);
            this.buttons.push(button);
            action.addPropertyChangeListener((action:API_action.Action) => {
                this.element.disabled = !action.isEnabled();
            });
            return button;
        }
    }
}
