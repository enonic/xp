module api.util.htmlarea.dialog {

    import FormItem = api.ui.form.FormItem;
    import Form = api.ui.form.Form;
    import FormView = api.form.FormView;
    import Validators = api.ui.form.Validators;
    import Panel = api.ui.panel.Panel;
    import DockedPanel = api.ui.panel.DockedPanel;
    import MacroDescriptor = api.macro.MacroDescriptor;
    import FormContext = api.form.FormContext;

    export class MacroModalDialog extends ModalDialog {

        private macroSelector: api.macro.MacroComboBox;
        private macroLoadMask: api.ui.mask.LoadMask;

        private static CONFIGURATION_TAB_NAME: string = "Configuration";
        private static PREVIEW_TAB_NAME: string = "Preview";

        private configPanel: Panel;
        private previewPanel: Panel;

        constructor(editor: HtmlAreaEditor) {
            super(editor, new api.ui.dialog.ModalDialogHeader("Insert Macro"), "macro-modal-dialog");
            this.macroLoadMask = new api.ui.mask.LoadMask(this);
        }

        protected layout() {
            super.layout();
            this.appendChildToContentPanel(this.createDockedPanelForSelectedItem());
        }

        private createDockedPanelForSelectedItem(): DockedPanel {
            var dockedPanel = new DockedPanel();
            dockedPanel.addItem(MacroModalDialog.CONFIGURATION_TAB_NAME, true, this.createConfigurationPanel());
            dockedPanel.addItem(MacroModalDialog.PREVIEW_TAB_NAME, true, this.createPreviewPanel());

            return dockedPanel;
        }

        private createConfigurationPanel(): Panel {
            return this.configPanel = new api.ui.panel.Panel();
        }

        private createPreviewPanel(): Panel {
            var panel = new api.ui.panel.Panel();
            panel.appendChild(new api.dom.LabelEl("Preview content"));
            return this.previewPanel = panel;
        }

        protected getMainFormItems(): FormItem[] {
            var macroSelector = this.createMacroSelector("macroId");

            this.setFirstFocusField(macroSelector.getInput());

            return [
                macroSelector
            ];
        }

        private createMacroSelector(id: string): FormItem {
            var loader = new api.macro.MacrosLoader(),
                macroSelector = api.macro.MacroComboBox.create().setLoader(loader).setMaximumOccurrences(1).build(),
                formItem = this.createFormItem(id, "Macro", Validators.required, api.util.StringHelper.EMPTY_STRING,
                    <api.dom.FormItemEl>macroSelector),
                macroSelectorComboBox = macroSelector.getComboBox();

            this.macroSelector = macroSelector;

            this.addClass("macro-selector");

            macroSelectorComboBox.onOptionSelected((selectedOption: api.ui.selector.combobox.SelectedOption<api.macro.MacroDescriptor>) => {
                formItem.addClass("selected-item-preview");
                this.addClass("shows-preview");

                var macroDescriptor: MacroDescriptor = selectedOption.getOption().displayValue;
                this.renderSelectedItemViews(macroDescriptor);
            });

            macroSelectorComboBox.onExpanded((event: api.ui.selector.DropdownExpandedEvent) => {
                if (event.isExpanded()) {
                    this.adjustSelectorDropDown(macroSelectorComboBox.getInput(), event.getDropdownElement().getEl());
                }
            });

            macroSelectorComboBox.onOptionDeselected(() => {
                formItem.removeClass("selected-item-preview");
                this.removeClass("shows-preview");
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

        private renderSelectedItemViews(macroDescriptor: MacroDescriptor) {
            this.renderConfigFormView(macroDescriptor);
            this.renderPreview(macroDescriptor);
        }

        private renderConfigFormView(macroDescriptor: MacroDescriptor) {
            this.configPanel.removeChildren();

            var formView: FormView = new FormView(FormContext.create().build(), macroDescriptor.getForm(), new api.data.PropertySet());
            formView.layout().then(() => {
                this.configPanel.appendChild(formView);
            });
        }

        private renderPreview(macroDescriptor: MacroDescriptor) {

        }

        protected initializeActions() {
            var submitAction = new api.ui.Action("Insert");
            this.setSubmitAction(submitAction);

            this.addAction(submitAction.onExecuted(() => {
                if (this.validate()) {

                    this.close();
                }
            }));

            super.initializeActions();
        }

        protected validate(): boolean {
            var mainFormValid = super.validate(),
                configPanelValid = this.validateConfigPanel();

            return mainFormValid && configPanelValid;
        }

        private validateConfigPanel(): boolean {
            var isValid = true,
                form = <FormView>(this.configPanel.getFirstChild());
            if (!!form) {
                isValid = form.validate(false).isValid();
                form.displayValidationErrors(!isValid);
            }
            return isValid;
        }

        private adjustSelectorDropDown(inputElement: api.dom.Element, dropDownElement: api.dom.ElementHelper) {
            var inputPosition = wemjq(inputElement.getHTMLElement()).offset();

            dropDownElement.setMaxWidthPx(inputElement.getEl().getWidthWithBorder() - 2);
            dropDownElement.setTopPx(inputPosition.top + inputElement.getEl().getHeightWithBorder() - 1);
            dropDownElement.setLeftPx(inputPosition.left);
        }
    }
}