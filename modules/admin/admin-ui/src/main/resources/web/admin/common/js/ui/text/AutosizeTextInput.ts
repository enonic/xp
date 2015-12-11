module api.ui.text {

    export class AutosizeTextInput extends TextInput {

        private attendant: api.dom.Element;
        private clone: api.dom.Element;

        constructor(className?: string, size: string = TextInput.MIDDLE, originalValue?: string) {
            super(className, size, originalValue);

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
            // Update input width according to current panel size.
            api.ui.responsive.ResponsiveManager.onAvailableSizeChanged(this, (item) => this.updateSize());
        }

        static large(className?: string, originalValue?: string): AutosizeTextInput {
            return new AutosizeTextInput(className, TextInput.LARGE, originalValue);
        }

        static middle(className?: string, originalValue?: string): AutosizeTextInput {
            return new AutosizeTextInput(className, TextInput.MIDDLE, originalValue);
        }

        private updateSize() {
            var inputEl = this.getEl(),
                cloneEl = this.clone.getEl();

            cloneEl.setFontSize(inputEl.getFontSize()).
                setPaddingLeft(inputEl.getPaddingLeft() + 'px').
                setPaddingRight(inputEl.getPaddingRight() + 'px');

            this.attendant.insertAfterEl(this);

            cloneEl.setInnerHtml(this.getValue());
            // Set input width to text length from the clone <div>
            // or to maximum possible width corresponding to attendant width.
            if (cloneEl.getWidthWithBorder() > this.attendant.getEl().getWidth()) {
                inputEl.setWidth("100%");
            } else {
                inputEl.setWidthPx(cloneEl.getWidthWithBorder());
            }


            this.attendant.remove();
        }

    }
}