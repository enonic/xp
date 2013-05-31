module API_ui_toolbar {

    export class Toolbar {

        ext;

        private buttons:Button[] = [];
        private element:HTMLElement;

        // TODO: create and expose HTML for tool bar

        constructor(actions:API_action.Action[]) {

            for (var i in actions) {
                this.addAction(actions[i]);
            }

            this.init();
        }

        private init() {
            this.ext = new Ext.Component({
                html: this.toHtml(),
                region: 'north'
            });
        }

        toHtml():string {
            var html = '';
            html += '<div id="toolbar">';
            for (var i in this.buttons) {
                html += this.buttons[i].toHTML();
            }
            html += '</div>';

            return html;
        }

        add(action:API_action.Action) {
            var btn = this.addAction(action);
            this.element.innerHTML += btn.toHTML();
            btn.afterRender();
        }

        afterRender() {
            this.element = document.getElementById('toolbar');
            for (var i in this.buttons) {
                this.buttons[i].afterRender();
            }
        }

        private addAction(action:API_action.Action):Button {

            var button = new Button(action);
            this.buttons.push(button);

            return button;
        }


    }
}
