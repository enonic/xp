module API_ui_toolbar {

    export class Button extends API_ui.Component {

        private action:API_action.Action;

        private element:HTMLElement;

        constructor(action:API_action.Action) {
            super('button');
            this.action = action;
            this.element = this.createHTMLElement();
            this.setEnable(action.isEnabled());

            action.addPropertyChangeListener((action:API_action.Action) => {
                this.setEnable(action.isEnabled());
            });
        }

        setEnable(value:bool) {
            this.element.disabled = !value;
        }

        setFloatRight(value:bool) {
            if( value ) {
                API_ui.HTMLElementHelper.addClass(this.element, 'pull-right');
            }
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
