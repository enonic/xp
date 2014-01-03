module api.app.wizard {

    export class WizardHeaderWithName extends WizardHeader {

        private nameEl:api.ui.TextInput;

        private nameTooltip:api.ui.Tooltip;

        constructor() {
            super();

            this.nameEl = api.ui.AutosizeTextInput.large().setName('name').setForbiddenCharsRe(/[^a-z0-9\-]+/ig);
            this.nameEl.addListener({
                onValueChanged: (oldValue, newValue) => {
                    this.notifyPropertyChanged("name", oldValue, newValue);
                }
            });
            this.appendChild(this.nameEl);

            this.nameTooltip = new api.ui.Tooltip(this.nameEl, "Name", 1000, 1000, api.ui.Tooltip.TRIGGER_FOCUS, api.ui.Tooltip.SIDE_RIGHT);
        }

        getName():string {
            return this.nameEl.getValue();
        }

        setName(value:string) {
            this.nameEl.setValue(value);
        }

        giveFocus() : boolean  {
            return this.nameEl.giveFocus();
        }
    }
}