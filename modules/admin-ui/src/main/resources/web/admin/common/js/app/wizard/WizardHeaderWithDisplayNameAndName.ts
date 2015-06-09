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

        private nameElWrapper: api.dom.DivEl;

        private nameEl: api.ui.text.TextInput;

        private autoGenerateName: boolean = false;

        private autoGenerationEnabled: boolean = true;

        private ignoreGenerateStatusForName: boolean = true;

        constructor(builder: WizardHeaderWithDisplayNameAndNameBuilder) {
            super();
            this.addClass("wizard-header-with-display-name-and-name");
            this.displayNameGenerator = builder.displayNameGenerator;
            this.displayNameProgrammaticallySet = this.displayNameGenerator != null;

            this.displayNameEl = api.ui.text.AutosizeTextInput.large().setName('displayName');
            this.displayNameEl.setPlaceholder("<Display Name>");
            this.displayNameEl.onValueChanged((event: api.ui.ValueChangedEvent) => {
                this.notifyPropertyChanged("displayName", event.getOldValue(), event.getNewValue());
            });
            this.appendChild(this.displayNameEl);

            this.pathEl = new api.dom.SpanEl('path');
            this.pathEl.hide();
            this.appendChild(this.pathEl);

            this.nameElWrapper = new api.dom.DivEl("name-wrapper");
            this.nameEl = api.ui.text.AutosizeTextInput.middle().setName('name').setForbiddenCharsRe(this.forbiddenChars);
            this.nameEl.setPlaceholder("<name>");
            this.nameEl.onValueChanged((event: api.ui.ValueChangedEvent) => {
                this.notifyPropertyChanged("name", event.getOldValue(), event.getNewValue());
            });
            this.nameElWrapper.appendChild(this.nameEl);
            this.appendChild(this.nameElWrapper);

            this.displayNameEl.onValueChanged((event: api.ui.ValueChangedEvent) => {

                this.displayNameEl.removeClass("generated");

                var currentDisplayName = event.getNewValue() || "";

                if (this.displayNameGenerator && this.displayNameGenerator.hasScript()) {
                    var generatedDisplayName = this.displayNameGenerator.execute() || "";

                    this.displayNameProgrammaticallySet =
                    generatedDisplayName.toLowerCase() === currentDisplayName.toLowerCase() ||
                    generatedDisplayName.trim().toLowerCase() === currentDisplayName.toLowerCase() ||
                    api.util.StringHelper.isEmpty(currentDisplayName);

                    if (this.displayNameProgrammaticallySet) {
                        this.displayNameEl.addClass("generated");
                    }
                }
                this.doAutoGenerateName(currentDisplayName);
            });

            this.nameEl.onValueChanged((event: api.ui.ValueChangedEvent) => {
                var currentName = event.getNewValue() || "";
                var displayName = this.getDisplayName() || "";

                this.autoGenerateName = this.checkAutoGenerateName(currentName, displayName);

                this.updateNameGeneratedStatus();
            });

            this.onShown((event) => this.updatePathAndNameWidth());
            api.dom.WindowDOM.get().onResized((event: UIEvent) => this.updatePathAndNameWidth(), this);

        }

        private checkAutoGenerateName(name: string, displayName: string): boolean {
            return api.util.StringHelper.isEmpty(name) ||
                   displayName.toLowerCase() === name.toLowerCase() ||
                   name.toLowerCase() === this.generateName(displayName).toLowerCase();
        }

        initNames(displayName: string, name: string, forceDisplayNameProgrammaticallySet: boolean) {

            this.autoGenerateName = this.checkAutoGenerateName(name, displayName);

            this.displayNameEl.setValue(displayName);
            if (name != null) {
                this.nameEl.setValue(name);
            } else {
                this.nameEl.setValue(this.generateName(displayName));
            }

            if (this.displayNameGenerator && this.displayNameGenerator.hasScript()) {
                if (!forceDisplayNameProgrammaticallySet) {
                    var generatedDisplayName = this.displayNameGenerator.execute();
                    this.displayNameProgrammaticallySet = generatedDisplayName == displayName;
                } else {
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
                value = value.trim();
                this.displayNameEl.setValue(value);
                this.doAutoGenerateName(value);
            }
        }

        setPath(value: string) {
            this.pathEl.getEl().setText(value);
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

        private setIgnoreGenerateStatusForName(value: boolean) {
            this.ignoreGenerateStatusForName = value;
            this.updateNameGeneratedStatus();
        }

        disableNameGeneration(value: boolean) {
            if (value) {
                this.setAutoGenerationEnabled(false);
                this.nameEl.getEl().setDisabled(true);
                this.nameElWrapper.onClicked(() => {
                    this.nameEl.getEl().setDisabled(false);
                    this.nameEl.giveFocus();
                });
                this.nameEl.onBlur(() => {
                    this.nameEl.getEl().setDisabled(true);
                });
            }
            this.setIgnoreGenerateStatusForName(value);
        }

        private updateNameGeneratedStatus() {
            if (this.autoGenerateName && !this.ignoreGenerateStatusForName) {
                this.nameEl.addClass("generated");
            } else {
                this.nameEl.removeClass("generated");
            }
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

        isValid(): boolean {
            return !!this.displayNameEl.getValue() && !!this.nameEl.getValue();
        }

    }
}