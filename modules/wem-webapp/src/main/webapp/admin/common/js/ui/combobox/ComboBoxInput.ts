module api_ui_combobox {

    export class ComboBoxInput extends api_ui.TextInput {

        constructor() {
            super();
            this.setPlaceholder("Type to search...");
        }

        setMaximumReached() {
            this.setPlaceholder("Maximum reached");
            this.getEl().setDisabled(true);
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