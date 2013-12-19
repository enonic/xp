module api_app_wizard {

    export class WizardHeaderWithDisplayNameAndNameBuilder {

        displayNameGenerator: DisplayNameGenerator;

        setDisplayNameGenerator(value: DisplayNameGenerator): WizardHeaderWithDisplayNameAndNameBuilder {
            this.displayNameGenerator = value;
            return this;
        }

        build(): WizardHeaderWithDisplayNameAndName {
            return new WizardHeaderWithDisplayNameAndName(this);
        }

    }

    export class WizardHeaderWithDisplayNameAndName extends WizardHeader implements api_event.Observable {

        private displayNameGenerator: DisplayNameGenerator;

        private forbiddenChars: RegExp = /[^a-z0-9\-]+/ig;

        private displayNameEl: api_ui.TextInput;

        private displayNameProgrammaticallySet: boolean;

        private pathEl: api_dom.SpanEl;

        private nameEl: api_ui.TextInput;

        private autoGenerateName: boolean = false;

        private nameTooltip: api_ui.Tooltip;

        private displayNameTooltip: api_ui.Tooltip;

        constructor(builder: WizardHeaderWithDisplayNameAndNameBuilder) {
            super();
            this.displayNameGenerator = builder.displayNameGenerator;
            this.displayNameProgrammaticallySet = this.displayNameGenerator != null;

            this.displayNameEl = api_ui.AutosizeTextInput.large().setName('displayName');
            this.displayNameEl.setPlaceholder("Display Name");
            this.displayNameEl.addListener({
                onValueChanged: (oldValue, newValue) => {
                    this.notifyPropertyChanged("displayName", oldValue, newValue);
                }
            });
            this.appendChild(this.displayNameEl);

            this.pathEl = new api_dom.SpanEl(null, 'path');
            this.pathEl.hide();
            this.appendChild(this.pathEl);

            this.nameEl = api_ui.AutosizeTextInput.middle().setName('name').setForbiddenCharsRe(this.forbiddenChars);
            this.nameEl.setPlaceholder("name");
            this.nameEl.addListener({
                onValueChanged: (oldValue, newValue) => {
                    this.notifyPropertyChanged("name", oldValue, newValue);
                }
            });
            this.appendChild(this.nameEl);

            this.displayNameEl.getEl().addEventListener('input', () => {

                var generatedDisplayName = this.displayNameGenerator.execute();
                var actualDisplayName = this.displayNameEl.getValue();
                this.displayNameProgrammaticallySet = generatedDisplayName == actualDisplayName;
                this.doAutoGenerateName(actualDisplayName);
            });

            this.nameEl.getEl().addEventListener('input', () => {
                var currentName = this.nameEl.getValue();

                var generatedName = this.generateName(this.getDisplayName());
                this.autoGenerateName = currentName == generatedName;
            });

            this.nameTooltip = new api_ui.Tooltip(this.nameEl, "Name", 1000, 1000, api_ui.Tooltip.TRIGGER_FOCUS, api_ui.Tooltip.SIDE_RIGHT);
            this.displayNameTooltip =
            new api_ui.Tooltip(this.displayNameEl, "Display name", 1000, 1000, api_ui.Tooltip.TRIGGER_FOCUS, api_ui.Tooltip.SIDE_RIGHT);
        }

        initNames(displayName: string, name: string) {

            if (displayName == name || name == this.generateName(displayName)) {
                this.autoGenerateName = true;
            }

            this.displayNameEl.setValue(displayName);
            if (name != null) {
                this.nameEl.setValue(name);
            }
            else {
                this.nameEl.setValue(this.generateName(displayName));
            }

            var generatedDisplayName = this.displayNameGenerator.execute();
            this.displayNameProgrammaticallySet = generatedDisplayName == displayName;
        }

        private doAutoGenerateName(value: string) {
            if (this.autoGenerateName) {
                this.nameEl.setValue(this.generateName(value));
            }
        }

        setDisplayName(value: string) {

            if( this.displayNameProgrammaticallySet ) {
                this.displayNameEl.setValue(value);
                this.doAutoGenerateName(value);
            }
            else {
                console.log("setDisplayName ignored: " + value);
            }
        }

        setPath(value: string) {
            this.pathEl.getEl().setInnerHtml(value);
            this.pathEl.getEl().setAttribute('title', value);
            if (value) {
                this.pathEl.show();
            } else {
                this.pathEl.hide();
            }
        }

        getName(): string {
            return this.nameEl.getValue();
        }

        getDisplayName(): string {
            return this.displayNameEl.getValue();
        }

        giveFocus(): boolean {
            return this.displayNameEl.giveFocus();
        }

        private generateName(value: string): string {
            if (!value) {
                return "";
            }

            var generated = value.replace(/[\s+\.\/]/ig, '-').replace(/-{2,}/g, '-').replace(/^-|-$/g, '').toLowerCase();
            return this.removeForbiddenChars(generated);
        }

        private removeForbiddenChars(rawValue: string): string {
            return this.forbiddenChars ? (rawValue || '').replace(this.forbiddenChars, '') : rawValue;
        }

    }
}