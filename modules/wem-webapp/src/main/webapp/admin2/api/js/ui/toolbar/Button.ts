module API_ui_toolbar {

    export class Button extends API_ui.Component {

        private action:API_action.Action;

        private element:HTMLElement;

        constructor(action:API_action.Action) {
            super('button');
            this.action = action;

            action.addPropertyChangeListener((action:API_action.Action) => {
                this.enable(action.isEnabled());
            });
        }

        enable(value:bool) {
            if (value) {
                this.element.className = 'enabled';
            } else {
                this.element.className = 'disabled';
            }
        }

        toHTMLElement():HTMLElement {
            var buttonEl:HTMLElement = document.createElement("button");
            buttonEl.id = super.getId();
            buttonEl.innerHTML = this.action.getLabel();
            this.element = buttonEl;
            this.element.addEventListener('click', () => {
                this.action.execute();
            });
            return buttonEl;
        }
    }
}
