module api.ui.selector.combobox {

    export class ComboBoxOptionFilterInput extends api.ui.selector.OptionFilterInput {

        constructor(placeholderText?: string) {
            super(placeholderText);
        }

        setMaximumReached() {
            this.setPlaceholder("Maximum reached");
            this.getEl().setDisabled(true);
        }

        getWidth(): number {
            return this.getEl().getWidth();
        }
    }

}
