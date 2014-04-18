module api.ui.toolbar {

    export class FoldButton extends api.ui.Button {

        private dropdown: api.dom.DivEl;
        private widthCache: number[] = [];

        constructor() {
            super("More");
            this.addClass('fold-button');

            this.dropdown = new api.dom.DivEl('dropdown');
            this.appendChild(this.dropdown);
        }

        push(element: api.dom.Element, width: number) {
            this.dropdown.prependChild(element);
            this.widthCache.unshift(width);
        }

        pop(): api.dom.Element {
            var top = this.dropdown.getFirstChild();
            this.dropdown.removeChild(top);
            this.widthCache.shift();
            return top;
        }

        getNextButtonWidth(): number {
            return this.widthCache[0];
        }

        getButtonsCount(): number {
            return this.dropdown.getChildren().length;
        }

        isEmpty(): boolean {
            return this.dropdown.getChildren().length == 0;
        }

    }

}