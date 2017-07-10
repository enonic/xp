module api.ui.selector {

    import i18n = api.util.i18n;

    export class OptionFilterInput extends api.ui.text.TextInput {

        private placeholderText: string;

        constructor(placeholderText?: string) {
            super('option-filter-input');
            this.placeholderText = placeholderText ? placeholderText : i18n('field.option.placeholder');

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
