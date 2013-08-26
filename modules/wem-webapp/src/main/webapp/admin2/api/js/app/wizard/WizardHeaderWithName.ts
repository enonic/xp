module api_app_wizard {

    export class WizardHeaderWithName extends WizardHeader {

        private nameEl:api_ui.TextInput;

        private nameTooltip:api_ui.Tooltip;

        constructor() {
            super();

            this.nameEl = api_ui.AutosizeTextInput.large().setName('name').setForbiddenCharsRe(/[^a-z0-9\-]+/ig);
            this.nameEl.addListener({
                onValueChanged: (oldValue, newValue) => {
                    this.notifyPropertyChanged("name", oldValue, newValue);
                }
            });
            this.appendChild(this.nameEl);

            this.nameTooltip = new api_ui.Tooltip(this.nameEl, "Name", 1000, 1000, api_ui.Tooltip.TRIGGER_FOCUS, api_ui.Tooltip.SIDE_RIGHT);
        }

        getName():string {
            return this.nameEl.getValue();
        }

        setName(value:string) {
            this.nameEl.setValue(value);
        }
    }
}