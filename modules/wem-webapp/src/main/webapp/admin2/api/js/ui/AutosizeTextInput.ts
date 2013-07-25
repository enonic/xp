module api_ui {

    export class AutosizeTextInput extends TextInput {

        private attendant: api_dom.Element;
        private clone: api_dom.Element;

        constructor(idPrefix?:string, className?:string, size?:string = TextInput.MIDDLE) {
            super(idPrefix, className, size);

            this.addClass('autosize');

            // Create <div> element with the same styles as this text input.
            // This clone <div> is displayed as inline element so its width matches to its text length.
            // Then input width will be updated according to text length from the div.
            this.clone = new api_dom.DivEl().addClass('autosize-clone').addClass(this.getEl().getAttribute('class'));
            // In order to have styles of clone div calculated it has to be in DOM.
            // Attendant element wraps the clone and has zero height
            // so it has calculated styles but isn't shown on a page.
            // Much more the attendant is displayed as block element and will be placed after this input.
            // Therefore it will have the maximum possible length.
            this.attendant = new api_dom.DivEl().addClass('autosize-attendant');
            this.attendant.appendChild(this.clone);

            // Update input width according to current text length.
            this.getEl().addEventListener('input', () => {
                this.updateSize();
            });

            // Update input width according to current page size.
            window.addEventListener('resize', () => {
                this.updateSize();
            });
        }

        static large(idPrefix?:string, className?:string):AutosizeTextInput {
            return new AutosizeTextInput(idPrefix, className, TextInput.LARGE);
        }

        static middle(idPrefix?:string, className?:string):AutosizeTextInput {
            return new AutosizeTextInput(idPrefix, className, TextInput.MIDDLE);
        }

        afterRender() {
            if (!this.isVisible()) {
                // If input isn't visible then append attendant element to body
                // in order to update input size according to initial text width.
                // Then insert attendant element after input for further size updates.
                api_dom.Body.get().appendChild(this.attendant);
                this.updateSize();
                this.attendant.insertAfterEl(this);
            }
            else {
                // If input is visible then insert attendant element after it
                // and calculate initial size according to text width.
                this.attendant.insertAfterEl(this);
                this.updateSize();
            }
        }

        setValue(value: string): AutosizeTextInput {
            super.setValue(value);
            this.updateSize();
            return this;
        }

        private updateSize() {
            var attendantEl = this.attendant.getEl();
            var cloneEl = this.clone.getEl();

            cloneEl.setInnerHtml(this.getValue());

            // Set input width to text length from the clone <div>
            // or to maximum possible width corresponding to attendant width.
            this.getEl().setWidth(Math.min(cloneEl.getWidth(), attendantEl.getWidth()) + 'px');
        }

    }
}