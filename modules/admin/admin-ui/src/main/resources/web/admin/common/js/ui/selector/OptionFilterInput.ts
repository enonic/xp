module api.ui.selector {

    export class OptionFilterInput extends api.ui.text.TextInput {

        private placeholderText: string;

        constructor(placeholderText?: string) {
            super('option-filter-input');
            this.placeholderText = placeholderText ? placeholderText : 'Type to search...';

            this.setPlaceholder(this.placeholderText);
        }

        openForTypingAndFocus() {
            this.openForTyping();
            this.getHTMLElement().focus();
        }

        openForTyping() {
            this.setPlaceholder(this.placeholderText);
            this.getEl().setDisabled(false);
        }
    }

}
