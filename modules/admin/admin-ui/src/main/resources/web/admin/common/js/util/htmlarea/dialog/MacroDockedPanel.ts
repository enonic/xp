module api.util.htmlarea.dialog {

    import MacroDescriptor = api.macro.MacroDescriptor;
    import MacroPreview = api.macro.MacroPreview;
    import FormView = api.form.FormView;
    import DockedPanel = api.ui.panel.DockedPanel;
    import Panel = api.ui.panel.Panel;
    import FormContext = api.form.FormContext;
    import Form = api.form.Form;

    import Input = api.form.Input;
    import FormItemSet = api.form.FormItemSet;
    import FieldSet = api.form.FieldSet;
    import FormItem = api.form.FormItem;
    import PropertySet = api.data.PropertySet;

    export class MacroDockedPanel extends DockedPanel {

        private static CONFIGURATION_TAB_NAME: string = "Configuration";
        private static PREVIEW_TAB_NAME: string = "Preview";
        private static MACRO_FORM_INCOMPLETE_MES: string = "Macro configuration is not complete";
        private static PREVIEW_LOAD_ERROR_MESSAGE: string = "An error occured while loading preview";

        private configPanel: Panel;
        private previewPanel: Panel;

        private contentPath: api.content.ContentPath;
        private macroDescriptor: MacroDescriptor;
        private previewResolved: boolean = false;
        private macroPreview: MacroPreview;
        private data: PropertySet;
        private macroLoadMask: api.ui.mask.LoadMask;

        private formValueChangedHandler: () => void;

        constructor(contentPath: api.content.ContentPath) {
            super();
            this.contentPath = contentPath;

            this.addItem(MacroDockedPanel.CONFIGURATION_TAB_NAME, true, this.createConfigurationPanel());
            this.addItem(MacroDockedPanel.PREVIEW_TAB_NAME, true, this.createPreviewPanel());

            this.macroLoadMask = new api.ui.mask.LoadMask(this.previewPanel);
            this.appendChild(this.macroLoadMask);

            this.handlePreviewPanelShowEvent();

            this.formValueChangedHandler = () => {
                this.previewResolved = false;
            };
        }

        private createConfigurationPanel(): Panel {
            return this.configPanel = new Panel("macro-config-panel");
        }

        private createPreviewPanel(): Panel {
            return this.previewPanel = new Panel("macro-preview-panel");
        }

        private handlePreviewPanelShowEvent() {
            this.previewPanel.onShown(() => {
                if (this.validateMacroForm()) {
                    if (!!this.macroDescriptor && !this.previewResolved) {
                        this.previewPanel.removeChildren();
                        this.fetchPreview().then((macroPreview: MacroPreview) => {
                            this.previewResolved = true;
                            this.macroPreview = macroPreview;
                            this.renderPreview(macroPreview);
                        }).catch((reason: any) => {
                            api.DefaultErrorHandler.handle(reason);
                            this.renderPreviewWithMessage(MacroDockedPanel.PREVIEW_LOAD_ERROR_MESSAGE);
                        }).finally(() => {
                            this.macroLoadMask.hide();
                        });
                    }
                } else {
                    this.renderPreviewWithMessage(MacroDockedPanel.MACRO_FORM_INCOMPLETE_MES);
                }
            });
        }

        private fetchPreview(): wemQ.Promise<MacroPreview> {
            this.macroLoadMask.show();

            return new api.macro.resource.GetPreviewRequest(
                new api.data.PropertyTree(this.data),
                this.macroDescriptor.getKey(),
                this.contentPath).
                sendAndParse();
        }

        private fetchMacroString(): wemQ.Promise<string> {
            this.macroLoadMask.show();

            return new api.macro.resource.GetPreviewStringRequest(new api.data.PropertyTree(this.data), this.macroDescriptor.getKey()).
                sendAndParse();
        }

        public getMacroPreviewString(): wemQ.Promise<string> {
            var deferred = wemQ.defer<string>();
            if (this.previewResolved) {
                deferred.resolve(this.macroPreview.getMacroString());
            } else {
                this.fetchMacroString().then((macroString: string) => {
                    deferred.resolve(macroString);
                }).catch((reason: any) => {
                    deferred.reject(reason);
                }).finally(() => {
                    this.macroLoadMask.hide();
                });
            }

            return deferred.promise;
        }

        private renderPreviewWithMessage(message: string) {
            this.previewPanel.removeChildren();
            var appendMe = new api.dom.DivEl("preview-message");
            appendMe.setHtml(message);
            this.previewPanel.appendChild(appendMe)
        }

        private renderPreview(macroPreview: MacroPreview) {
            var appendMe = new api.dom.DivEl("preview-content");
            appendMe.setHtml(macroPreview.getHtml(), false);
            this.previewPanel.appendChild(appendMe)
        }

        public validateMacroForm(): boolean {
            var isValid = true,
                form = <FormView>(this.configPanel.getFirstChild());
            if (!!form) {
                isValid = form.validate(false).isValid();
                form.displayValidationErrors(!isValid);
            }
            return isValid;
        }

        public setMacroDescriptor(macroDescriptor: MacroDescriptor) {
            this.macroDescriptor = macroDescriptor;
            this.previewResolved = false;

            this.initPropertySetForDescriptor(macroDescriptor);
            this.showDescriptorConfigView(macroDescriptor);
        }

        private showDescriptorConfigView(macroDescriptor: MacroDescriptor) {
            this.selectPanel(this.configPanel);

            if (!!macroDescriptor) {
                var formView: FormView = new FormView(FormContext.create().build(), macroDescriptor.getForm(), this.data);
                this.renderConfigView(formView)
            }
        }

        private initPropertySetForDescriptor(macroDescriptor: MacroDescriptor) {
            if (!!this.data) {
                this.data.unPropertyValueChanged(this.formValueChangedHandler);
            }
            this.data = this.generateBackingPropertySetForForm(macroDescriptor.getForm());
            this.data.onPropertyValueChanged(this.formValueChangedHandler);
        }

        private generateBackingPropertySetForForm(form: Form): PropertySet {
            var propertySet = new PropertySet();
            this.populatePropertySetWithFormItems(form.getFormItems(), propertySet);
            return propertySet;
        }

        private populatePropertySetWithFormItems(formItems: FormItem[], propertySet: PropertySet) {
            formItems.forEach((formItem: FormItem) => {

                if (api.ObjectHelper.iFrameSafeInstanceOf(formItem, FormItemSet)) {
                    this.populatePropertySetWithFormItems((<FormItemSet>formItem).getFormItems(), propertySet);
                }
                else if (api.ObjectHelper.iFrameSafeInstanceOf(formItem, FieldSet)) {
                    this.populatePropertySetWithFormItems((<FieldSet>formItem).getFormItems(), propertySet);
                }
                else if (api.ObjectHelper.iFrameSafeInstanceOf(formItem, Input)) {
                    var input: Input = <Input>formItem;

                    propertySet.addProperty(input.getName(), api.data.ValueTypes.STRING.newNullValue());
                }
            });
        }

        private renderConfigView(formView: FormView) {
            this.configPanel.removeChildren();

            formView.layout().then(() => {
                this.configPanel.appendChild(formView);
                api.ui.responsive.ResponsiveManager.fireResizeEvent();
            });
        }
    }
}