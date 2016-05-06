module api.util.htmlarea.dialog {

    import FormItem = api.ui.form.FormItem;
    import Validators = api.ui.form.Validators;

    export class MacroModalDialog extends ModalDialog {

        private macroSelector: api.macro.MacroComboBox;
        private macroLoadMask: api.ui.mask.LoadMask;

        constructor(editor: HtmlAreaEditor) {
            super(editor, new api.ui.dialog.ModalDialogHeader("Insert Macro"), "macro-modal-dialog");
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

            formItem.addClass("macro-selector");

            if (this.macroSelector) {
                var singleLoadListener = (event: api.util.loader.event.LoadedDataEvent<api.macro.MacroDescriptor>) => {
                    loader.unLoadedData(singleLoadListener);
                };
                loader.onLoadedData(singleLoadListener);
                loader.load();
            }

            macroSelectorComboBox.onOptionSelected((selectedOption: api.ui.selector.combobox.SelectedOption<api.macro.MacroDescriptor>) => {
                formItem.addClass("macro-preview");
            });

            macroSelectorComboBox.onExpanded((event: api.ui.selector.DropdownExpandedEvent) => {
                if (event.isExpanded()) {
                    this.adjustSelectorDropDown(macroSelectorComboBox.getInput(), event.getDropdownElement().getEl());
                }
            });

            macroSelectorComboBox.onOptionDeselected(() => {
                formItem.removeClass("macro-preview");
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
                if (this.validate()) {
                    //
                    this.close();
                }
            }));

            super.initializeActions();
        }

        private adjustSelectorDropDown(inputElement: api.dom.Element, dropDownElement: api.dom.ElementHelper) {
            var inputPosition = wemjq(inputElement.getHTMLElement()).offset();

            dropDownElement.setMaxWidthPx(inputElement.getEl().getWidthWithBorder() - 2);
            dropDownElement.setTopPx(inputPosition.top + inputElement.getEl().getHeightWithBorder() - 1);
            dropDownElement.setLeftPx(inputPosition.left);
        }
    }
}