module api.ui.selector {

    export class OptionFilterInput extends api.ui.TextInput {

        constructor() {
            super("option-filter-input");
            this.setPlaceholder("Type to search...");
        }

        openForTypingAndFocus() {
            this.openForTyping();
            this.getHTMLElement().focus();
        }

        openForTyping() {
            this.setPlaceholder("Type to search...");
            this.getEl().setDisabled(false);
        }
    }

}