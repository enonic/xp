module api.util.htmlarea.dialog {

    import FormItem = api.ui.form.FormItem;
    import Validators = api.ui.form.Validators;
    import Panel = api.ui.panel.Panel;
    import MacroDescriptor = api.macro.MacroDescriptor;
    import FormContext = api.form.FormContext;
    import ApplicationKey = api.application.ApplicationKey;
    import SelectedOptionEvent = api.ui.selector.combobox.SelectedOptionEvent;

    export class MacroModalDialog extends ModalDialog {

        private macroDockedPanel: MacroDockedPanel;

        private macroSelector: api.macro.MacroComboBox;

        private callback: Function;

        constructor(config: HtmlAreaMacro, content: api.content.ContentSummary, applicationKeys: ApplicationKey[]) {
            super(config.editor, 'Insert Macro', 'macro-modal-dialog');

            this.callback = config.callback;

            this.macroSelector.getLoader().setApplicationKeys(applicationKeys);
            this.macroDockedPanel.setContent(content);
        }

        protected layout() {
            super.layout();
            this.appendChildToContentPanel(this.macroDockedPanel = this.makeMacroDockedPanel());
        }

        private makeMacroDockedPanel(): MacroDockedPanel {
            let macroDockedPanel = new MacroDockedPanel();

            let debouncedPreviewRenderedHandler: () => void = api.util.AppHelper.debounce(() => {
                this.centerMyself();
            }, 400, false);

            macroDockedPanel.onPanelRendered(debouncedPreviewRenderedHandler);
            this.onRemoved(() => {
                macroDockedPanel.unPanelRendered(debouncedPreviewRenderedHandler);
            });

            return macroDockedPanel;
        }

        protected getMainFormItems(): FormItem[] {
            let macroSelector = this.createMacroSelector('macroId');

            this.setFirstFocusField(macroSelector.getInput());

            return [
                macroSelector
            ];
        }

        private createMacroSelector(id: string): FormItem {
            let loader = new api.macro.resource.MacrosLoader();
            let macroSelector = api.macro.MacroComboBox.create().setLoader(loader).setMaximumOccurrences(1).build();
            let formItem = this.createFormItem(id, 'Macro', Validators.required, api.util.StringHelper.EMPTY_STRING,
                    <api.dom.FormItemEl>macroSelector);
            let macroSelectorComboBox = macroSelector.getComboBox();

            this.macroSelector = macroSelector;
            this.addClass('macro-selector');

            macroSelectorComboBox.onOptionSelected((event: SelectedOptionEvent<api.macro.MacroDescriptor>) => {
                formItem.addClass('selected-item-preview');
                this.addClass('shows-preview');

                this.macroDockedPanel.setMacroDescriptor(event.getSelectedOption().getOption().displayValue);
            });

            macroSelectorComboBox.onOptionDeselected(() => {
                formItem.removeClass('selected-item-preview');
                this.removeClass('shows-preview');
                this.displayValidationErrors(false);
                api.ui.responsive.ResponsiveManager.fireResizeEvent();
            });

            return formItem;
        }

        protected initializeActions() {
            let submitAction = new api.ui.Action('Insert');
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
                let macro = this.callback(api.util.StringHelper.escapeHtml(macroString));
                this.close();
            }).catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
                api.notify.showError('Failed to generate macro.');
            });
        }

        protected validate(): boolean {
            let mainFormValid = super.validate();
            let configPanelValid = this.macroDockedPanel.validateMacroForm();

            return mainFormValid && configPanelValid;
        }
    }
}
