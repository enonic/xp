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

    export class WizardHeaderWithDisplayNameAndName extends WizardHeader {

        private displayNameGenerator: DisplayNameGenerator;

        private forbiddenChars: RegExp = /[\/\\]+/ig;

        private displayNameEl: api.ui.text.TextInput;

        private displayNameProgrammaticallySet: boolean;

        private pathEl: api.dom.SpanEl;

        private nameEl: api.ui.text.TextInput;

        private autoGenerateName: boolean = false;

        private autoGenerationEnabled: boolean = true;

        constructor(builder: WizardHeaderWithDisplayNameAndNameBuilder) {
            super();
            this.displayNameGenerator = builder.displayNameGenerator;
            this.displayNameProgrammaticallySet = this.displayNameGenerator != null;

            this.displayNameEl = api.ui.text.AutosizeTextInput.large().setName('displayName');
            this.displayNameEl.setPlaceholder("Display Name");
            this.displayNameEl.onValueChanged((event: api.ui.ValueChangedEvent) => {
                this.notifyPropertyChanged("displayName", event.getOldValue(), event.getNewValue());
            });
            this.appendChild(this.displayNameEl);

            this.pathEl = new api.dom.SpanEl('path');
            this.pathEl.hide();
            this.appendChild(this.pathEl);

            this.nameEl = api.ui.text.AutosizeTextInput.middle().setName('name').setForbiddenCharsRe(this.forbiddenChars);
            this.nameEl.setPlaceholder("name");
            this.nameEl.onValueChanged((event: api.ui.ValueChangedEvent) => {
                this.notifyPropertyChanged("name", event.getOldValue(), event.getNewValue());
            });
            this.appendChild(this.nameEl);

            this.displayNameEl.onValueChanged((event: api.ui.ValueChangedEvent) => {

                var currentDisplayName = event.getNewValue();

                if (this.displayNameGenerator && this.displayNameGenerator.hasScript()) {
                    var generatedDisplayName = this.displayNameGenerator.execute();

                    this.displayNameProgrammaticallySet =
                    generatedDisplayName == currentDisplayName || api.util.StringHelper.isEmpty(currentDisplayName);
                }
                this.doAutoGenerateName(currentDisplayName);
            });

            this.nameEl.onValueChanged((event: api.ui.ValueChangedEvent) => {
                var currentName = event.getNewValue();
                var generatedName = this.generateName(this.getDisplayName());

                this.autoGenerateName = this.checkAutoGenerateName(currentName, generatedName);
            });

            this.onShown((event) => this.updatePathAndNameWidth());
            api.dom.WindowDOM.get().onResized((event: UIEvent) => this.updatePathAndNameWidth(), this);

        }

        private checkAutoGenerateName(name: string, displayName: string): boolean {
            return api.util.StringHelper.isEmpty(name) || displayName == name || name == this.generateName(displayName);
        }

        initNames(displayName: string, name: string, forceDisplayNameProgrammaticallySet: boolean) {

            this.autoGenerateName = this.checkAutoGenerateName(name, displayName);

            this.displayNameEl.setValue(displayName);
            if (name != null) {
                this.nameEl.setValue(name);
            }
            else {
                this.nameEl.setValue(this.generateName(displayName));
            }

            if (this.displayNameGenerator && this.displayNameGenerator.hasScript()) {
                if (!forceDisplayNameProgrammaticallySet) {
                    var generatedDisplayName = this.displayNameGenerator.execute();
                    this.displayNameProgrammaticallySet = generatedDisplayName == displayName;
                }
                else {
                    this.displayNameProgrammaticallySet = true;
                }
            }
        }

        private doAutoGenerateName(value: string) {
            if (this.autoGenerateName && this.autoGenerationEnabled) {
                this.nameEl.setValue(this.generateName(value));
            }
        }

        isAutoGenerationEnabled(): boolean {
            return this.autoGenerationEnabled;
        }

        setAutoGenerationEnabled(value: boolean) {
            this.autoGenerationEnabled = value;
        }

        setDisplayName(value: string) {
            if (this.displayNameProgrammaticallySet) {
                this.displayNameEl.setValue(value);
                this.doAutoGenerateName(value);
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

        disableNameInput() {
            this.nameEl.getEl().setAttribute("disabled", "disabled");
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
            return api.Name.ensureValidName(value);
        }

        private updatePathAndNameWidth() {
            var pathEl = this.pathEl.getEl(),
                nameEl = this.nameEl.getEl(),
                headerWidth = this.getEl().getWidth(),
                pathWidth = pathEl.getWidthWithMargin(),
                nameWidth = nameEl.getWidthWithMargin(),
                nameMinWidth = nameEl.getMinWidth();

            if (pathWidth + nameWidth > headerWidth) {
                if (nameWidth > nameMinWidth) {
                    nameEl.setWidthPx(Math.max(nameMinWidth, headerWidth - pathWidth));
                }
                if (pathWidth + nameMinWidth > headerWidth) {
                    pathEl.setWidthPx(headerWidth - nameMinWidth - pathEl.getMarginLeft() - pathEl.getMarginRight());
                }
            } else {
                var pathClone = new api.dom.SpanEl('path');
                pathClone.setHtml(pathEl.getInnerHtml());
                pathClone.getEl().setHeight('0px');
                pathClone.insertAfterEl(this.pathEl);
                var pathCloneWidth = pathClone.getEl().getWidth();

                if (pathEl.getWidth() < pathCloneWidth) {
                    pathEl.setWidthPx(Math.min(pathCloneWidth, headerWidth - nameWidth - pathEl.getMarginLeft() - pathEl.getMarginRight()));
                }
                pathClone.remove();
            }
        }

    }
}