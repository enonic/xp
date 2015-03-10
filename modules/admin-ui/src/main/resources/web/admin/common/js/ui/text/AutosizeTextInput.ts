module api.ui.text {

    export class AutosizeTextInput extends TextInput {

        private attendant: api.dom.Element;
        private clone: api.dom.Element;

        constructor(className?: string, size: string = TextInput.MIDDLE) {
            super(className, size);

            this.addClass('autosize');

            // Create <div> element with the same styles as this text input.
            // This clone <div> is displayed as inline element so its width matches to its text length.
            // Then input width will be updated according to text length from the div.
            this.clone = new api.dom.DivEl().addClass('autosize-clone').addClass(this.getEl().getAttribute('class'));
            // In order to have styles of clone div calculated it has to be in DOM.
            // Attendant element wraps the clone and has zero height
            // so it has calculated styles but isn't shown on a page.
            // Much more the attendant is displayed as block element and will be placed after this input.
            // Therefore it will have the maximum possible length.
            this.attendant = new api.dom.DivEl().addClass('autosize-attendant');
            this.attendant.appendChild(this.clone);

            // Update input after input has been shown.
            this.onShown((event) => this.updateSize());

            // Update input width according to current text length.
            this.onValueChanged((event) => this.updateSize());

            // Update input width according to current page size.
            api.dom.WindowDOM.get().onResized((event: UIEvent) => this.updateSize(), this);
        }

        static large(className?: string): AutosizeTextInput {
            return new AutosizeTextInput(className, TextInput.LARGE);
        }

        static middle(className?: string): AutosizeTextInput {
            return new AutosizeTextInput(className, TextInput.MIDDLE);
        }

        private updateSize() {
            var inputEl = this.getEl(),
                cloneEl = this.clone.getEl();

            cloneEl.setFontSize(inputEl.getFontSize()).
                setPaddingLeft(inputEl.getPaddingLeft() + 'px').
                setPaddingRight(inputEl.getPaddingRight() + 'px');

            this.attendant.insertAfterEl(this);

            cloneEl.setInnerHtml(this.getValue(), true);
            // Set input width to text length from the clone <div>
            // or to maximum possible width corresponding to attendant width.
            inputEl.setWidthPx(Math.min(cloneEl.getWidthWithBorder(), this.attendant.getEl().getWidth()));

            this.attendant.remove();
        }

    }
}