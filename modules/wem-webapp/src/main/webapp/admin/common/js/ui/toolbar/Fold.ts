module api.ui.toolbar {

    export class Fold extends api.dom.DivEl {

        private dropdown: api.dom.DivEl;

        constructor() {
            super('fold');

            this.dropdown = new api.dom.DivEl('dropdown');
            this.appendChild(this.dropdown);
        }

        push(element: api.dom.Element) {
            this.dropdown.prependChild(element);
        }

        pop(): api.dom.Element {
            var top = this.dropdown.getFirstChild();
            this.dropdown.removeChild(top);
            return top;
        }

        isEmpty():boolean {
            return this.dropdown.getChildren().length == 0;
        }

    }

}