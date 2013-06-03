module API_ui_toolbar {

    export class Toolbar extends API_ui.Component {

        ext;

        private buttons:Button[] = [];

        private element:HTMLElement;

        constructor() {
            super("toolbar");
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
            divEl.className = 'toolbar';
            for (var i in this.buttons) {
                divEl.appendChild(this.buttons[i].getHTMLElement());
            }
            return divEl;
        }

        addAction(action:API_action.Action) {
            var button:Button = this.doAddAction(action);
            this.element.appendChild(button.getHTMLElement())
        }

        private doAddAction(action:API_action.Action):Button {
            var button = new Button(action);
            this.buttons.push(button);
            return button;
        }
    }
}
