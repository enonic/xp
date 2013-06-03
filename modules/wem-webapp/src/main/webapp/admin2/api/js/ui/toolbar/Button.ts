module API_ui_toolbar {

    export class Button extends API_ui.Component {

        private action:API_action.Action;

        private element:HTMLElement;

        constructor(action:API_action.Action) {
            super('button');
            this.action = action;
            this.element = this.createHTMLElement();
            this.enable(action.isEnabled());

            action.addPropertyChangeListener((action:API_action.Action) => {
                this.enable(action.isEnabled());
            });
        }

        enable(value:bool) {
            this.element.disabled = !value;
        }

        getHTMLElement():HTMLElement {
            return this.element;
        }

        private createHTMLElement():HTMLElement {
            var buttonEl:HTMLElement = document.createElement("button");
            buttonEl.id = super.getId();
            buttonEl.innerHTML = this.action.getLabel();
            buttonEl.addEventListener('click', () => {
                this.action.execute();
            });
            return buttonEl;
        }
    }
}
