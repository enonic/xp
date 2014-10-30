module api.ui.security {

    interface AccessOption {
        value: string;
        name: string;
    }

    /**
     * Wrapper around api.ui.Dropdown to have custom arrow
     */
    export class AccessSelector extends api.dom.DivEl {

        private static ACCESS_OPTIONS: AccessOption[] = [
            {value:'read', name:'Can Read'},
            {value:'write', name:'Can Write'},
            {value:'publish', name:'Can Publish'},
            {value:'full', name:'Full Access'},
            {value:'custom', name:'Custom...'}
        ];

        private dropdown: api.ui.Dropdown;

        constructor() {
            super('access-selector');

            this.dropdown = new api.ui.Dropdown('access-selector');
            AccessSelector.ACCESS_OPTIONS.forEach((option: AccessOption) => this.dropdown.addOption(option.value, option.name));
            this.appendChild(this.dropdown);

            var label = new api.dom.LabelEl('', this.dropdown, 'icon-arrow-down2');
            this.appendChild(label);
        }

        getValue(): string {
            return this.dropdown.getValue();
        }

        setValue(value: string) {
            this.dropdown.setValue(value);
        }

        onValueChanged(listener: (event: ValueChangedEvent)=>void) {
            this.dropdown.onValueChanged(listener);
        }

        unValueChanged(listener: (event: ValueChangedEvent)=>void) {
            this.dropdown.unValueChanged(listener);
        }

    }

}