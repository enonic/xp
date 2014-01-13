module api.app.wizard {

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

    export class WizardHeaderWithDisplayNameAndName extends WizardHeader implements api.event.Observable {

        private displayNameGenerator: DisplayNameGenerator;

        private forbiddenChars: RegExp = /[^a-z0-9\-]+/ig;

        private displayNameEl: api.ui.TextInput;

        private displayNameProgrammaticallySet: boolean;

        private pathEl: api.dom.SpanEl;

        private nameEl: api.ui.TextInput;

        private autoGenerateName: boolean = false;

        constructor(builder: WizardHeaderWithDisplayNameAndNameBuilder) {
            super();
            this.displayNameGenerator = builder.displayNameGenerator;
            this.displayNameProgrammaticallySet = this.displayNameGenerator != null;

            this.displayNameEl = api.ui.AutosizeTextInput.large().setName('displayName');
            this.displayNameEl.setPlaceholder("Display Name");
            this.displayNameEl.addListener({
                onValueChanged: (oldValue, newValue) => {
                    this.notifyPropertyChanged("displayName", oldValue, newValue);
                }
            });
            this.appendChild(this.displayNameEl);

            this.pathEl = new api.dom.SpanEl(null, 'path');
            this.pathEl.hide();
            this.appendChild(this.pathEl);

            this.nameEl = api.ui.AutosizeTextInput.middle().setName('name').setForbiddenCharsRe(this.forbiddenChars);
            this.nameEl.setPlaceholder("name");
            this.nameEl.addListener({
                onValueChanged: (oldValue, newValue) => {
                    this.notifyPropertyChanged("name", oldValue, newValue);
                }
            });
            this.appendChild(this.nameEl);

            this.displayNameEl.getEl().addEventListener('input', () => {

                var actualDisplayName = this.displayNameEl.getValue();

                if (this.displayNameGenerator.hasScript()) {
                    var generatedDisplayName = this.displayNameGenerator.execute();

                    this.displayNameProgrammaticallySet =
                    generatedDisplayName == actualDisplayName || api.util.isStringEmpty(actualDisplayName);
                    console.log("*** DisplayName manually changed to [" + actualDisplayName + "] - generated is [" + generatedDisplayName +
                                "], this.displayNameProgrammaticallySet = " +
                                this.displayNameProgrammaticallySet);
                }
                this.doAutoGenerateName(actualDisplayName);
            });

            this.nameEl.getEl().addEventListener('input', () => {
                var currentName = this.nameEl.getValue();

                var generatedName = this.generateName(this.getDisplayName());
                this.autoGenerateName = currentName == generatedName;
            });

        }

        initNames(displayName: string, name: string, forceDisplayNameProgrammaticallySet: boolean) {

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

            if (this.displayNameGenerator.hasScript()) {
                if (!forceDisplayNameProgrammaticallySet) {
                    var generatedDisplayName = this.displayNameGenerator.execute();
                    this.displayNameProgrammaticallySet = generatedDisplayName == displayName;
                    console.log("*** initNames(" + displayName + ", " + name + ") - generated is [" + generatedDisplayName +
                                "]: displayNameProgrammaticallySet to: " +
                                this.displayNameProgrammaticallySet);
                }
                else {
                    this.displayNameProgrammaticallySet = true;
                    console.log("*** initNames(" + displayName + ", " + name + "): Forced displayNameProgrammaticallySet to true");
                }
            }
        }

        private doAutoGenerateName(value: string) {
            if (this.autoGenerateName) {
                this.nameEl.setValue(this.generateName(value));
            }
        }

        setDisplayName(value: string) {

            if (this.displayNameProgrammaticallySet) {
                this.displayNameEl.setValue(value);
                this.doAutoGenerateName(value);
            }
            else {
                console.log("*** setDisplayName ignored: " + value);
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
            return api.content.ContentName.ensureValidName(value);
        }

    }
}