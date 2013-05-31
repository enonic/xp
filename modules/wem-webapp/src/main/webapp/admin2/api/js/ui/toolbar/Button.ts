module API_ui_toolbar {

    export class Button {

        private static counstructorCounter:number = 0;

        private action:API_action.Action;
        private id:string;
        private element:HTMLElement;

        constructor(action:API_action.Action) {
            this.action = action;
            this.id = 'button-' + ++Button.counstructorCounter;


            action.addPropertyChangeListener((action:API_action.Action) => {
                // TODO: refresh ui
                this.enable(action.isEnabled());
            });
        }

        enable(isEnabled:bool) {
            if (isEnabled) {
                this.element.className = 'enabled';
            } else {
                this.element.className = 'disabled';
            }

        }

        toHTML():string {
            var html = '';
            html += '<button id="' + this.id + '">';
            html += this.action.getLabel();
            html += '</button>';
            return html;
        }

        afterRender() {
            this.element = document.getElementById(this.id);

            this.element.addEventListener('click', () => {
                this.action.execute();
            });
        }

    }
}
