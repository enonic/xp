module api.util.htmlarea.dialog {

    import FormItem = api.ui.form.FormItem;
    import Validators = api.ui.form.Validators;
    import Panel = api.ui.panel.Panel;
    import MacroDescriptor = api.macro.MacroDescriptor;
    import FormContext = api.form.FormContext;
    import ApplicationKey = api.application.ApplicationKey

    export class MacroModalDialog extends ModalDialog {

        private contentPath: api.content.ContentPath;

        private applicationKeys: ApplicationKey[];

        private macroDockedPanel: MacroDockedPanel;

        private callback: Function;

        constructor(config: HtmlAreaMacro, contentPath: api.content.ContentPath, applicationKeys: ApplicationKey[]) {
            this.contentPath = contentPath;
            this.applicationKeys = applicationKeys;
            this.callback = config.callback;
            super(config.editor, new api.ui.dialog.ModalDialogHeader("Insert Macro"), "macro-modal-dialog");
        }

        protected layout() {
            super.layout();
            this.appendChildToContentPanel(this.macroDockedPanel = new MacroDockedPanel(this.contentPath));
        }

        protected getMainFormItems(): FormItem[] {
            var macroSelector = this.createMacroSelector("macroId");

            this.setFirstFocusField(macroSelector.getInput());

            return [
                macroSelector
            ];
        }

        private createMacroSelector(id: string): FormItem {
            var loader = new api.macro.resource.MacrosLoader(this.applicationKeys),
                macroSelector = api.macro.MacroComboBox.create().setLoader(loader).setMaximumOccurrences(1).build(),
                formItem = this.createFormItem(id, "Macro", Validators.required, api.util.StringHelper.EMPTY_STRING,
                    <api.dom.FormItemEl>macroSelector),
                macroSelectorComboBox = macroSelector.getComboBox();

            this.addClass("macro-selector");

            macroSelectorComboBox.onOptionSelected((selectedOption: api.ui.selector.combobox.SelectedOption<api.macro.MacroDescriptor>) => {
                formItem.addClass("selected-item-preview");
                this.addClass("shows-preview");

                this.macroDockedPanel.setMacroDescriptor(selectedOption.getOption().displayValue);
            });

            macroSelectorComboBox.onExpanded((event: api.ui.selector.DropdownExpandedEvent) => {
                if (event.isExpanded()) {
                    this.adjustSelectorDropDown(macroSelectorComboBox.getInput(), event.getDropdownElement().getEl());
                }
            });

            macroSelectorComboBox.onOptionDeselected(() => {
                formItem.removeClass("selected-item-preview");
                this.removeClass("shows-preview");
                this.displayValidationErrors(false);
                api.ui.responsive.ResponsiveManager.fireResizeEvent();
            });

            macroSelectorComboBox.onKeyDown((e: KeyboardEvent) => {
                if (api.ui.KeyHelper.isEscKey(e) && !macroSelectorComboBox.isDropdownShown()) {
                    // Prevent modal dialog from closing on Esc key when dropdown is expanded
                    e.preventDefault();
                    e.stopPropagation();
                }
            });

            return formItem;
        }

        protected initializeActions() {
            var submitAction = new api.ui.Action("Insert");
            this.setSubmitAction(submitAction);

            this.addAction(submitAction.onExecuted(() => {
                this.displayValidationErrors(true);
                if (this.validate()) {
                    this.insertMacroIntoTextArea();
                }
            }));

            super.initializeActions();
        }

        private insertMacroIntoTextArea(): void {
            this.macroDockedPanel.getMacroPreviewString().then((macroString: string) => {
                var macro = this.callback(macroString);
                this.close();
            }).catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
                api.notify.showError("Failed to generate macro.");
            });
        }

        protected validate(): boolean {
            var mainFormValid = super.validate(),
                configPanelValid = this.macroDockedPanel.validateMacroForm();

            return mainFormValid && configPanelValid;
        }

        private adjustSelectorDropDown(inputElement: api.dom.Element, dropDownElement: api.dom.ElementHelper) {
            var inputPosition = wemjq(inputElement.getHTMLElement()).offset();

            dropDownElement.setMaxWidthPx(inputElement.getEl().getWidthWithBorder() - 2);
            dropDownElement.setTopPx(inputPosition.top + inputElement.getEl().getHeightWithBorder() - 1);
            dropDownElement.setLeftPx(inputPosition.left);
        }
    }
}