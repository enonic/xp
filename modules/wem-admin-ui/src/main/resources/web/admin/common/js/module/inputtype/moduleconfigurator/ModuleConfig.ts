module api.module.inputtype.moduleconfigurator {

    import AEl = api.dom.AEl;
    import Option = api.ui.selector.Option;
    import Value = api.data.Value;
    import FormView = api.form.FormView;
    import FormContextBuilder = api.form.FormContextBuilder;
    import Module = api.module.Module;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;

    export class ModuleConfig extends api.dom.DivEl {

        private comboBox: ModuleComboBox;

        private selectedOption: Option<Module>;

        private selectedModule: Module;

        private formView: FormView;

        private moduleSelectedListeners: {(event: ModuleSelectedEvent): void;}[];

        constructor() {
            super("module-config");

            this.moduleSelectedListeners = [];

            this.comboBox = new ModuleComboBox(1);
            this.comboBox.onOptionSelected((event: OptionSelectedEvent<Module>) => {
                this.selectedOption = event.getOption();
                this.selectedModule = event.getOption().displayValue;
                this.addFormView();
            });

            this.comboBox.onSelectedOptionRemoved(() => {
                this.removeFormView();
            });

            this.appendChild(this.comboBox);
        }

        addFormView() {
            if (!!this.selectedModule) {
                this.comboBox.hide();

                if (this.formView) {
                    this.removeChild(this.formView);
                    this.formView = null;
                }

                var formContext = new FormContextBuilder().build();
                var configData = new api.data.RootDataSet();
                var configForm = this.selectedModule.getForm();

                this.formView = new FormView(formContext, configForm, configData);
                this.formView.setDoOffset(false);
                this.formView.addClass("module-form");
                this.appendChild(this.formView);

                var removeButton = new api.dom.AEl("remove-button remove-module");
                this.formView.appendChild(removeButton);
                removeButton.onClicked((event: MouseEvent) => {
                    this.comboBox.deselect(this.selectedOption);
                });

                this.notifyOptionSelected(new ModuleSelectedEvent(this.selectedModule, this.formView));
            }
        }

        removeFormView() {
            this.comboBox.show();

            if (this.formView) {
                this.removeChild(this.formView);
                this.formView = null;
            }

            this.selectedModule = null;
            this.selectedOption = null;
        }

        getComboBox(): ModuleComboBox {
            return this.comboBox;
        }

        getSelectedOption(): Option<Module> {
            return this.selectedOption;
        }

        getSelectedModule(): Module {
            return this.selectedModule;
        }

        getFormView(): FormView {
            return this.formView;
        }

        onModuleSelected(listener: {(event: ModuleSelectedEvent): void;}) {
            this.moduleSelectedListeners.push(listener);
        }

        unModuleSelected(listener: {(event: ModuleSelectedEvent): void;}) {
            this.moduleSelectedListeners.filter((currentListener: (event: ModuleSelectedEvent) =>void) => {
                return listener != currentListener;
            });
        }

        private notifyOptionSelected(event: ModuleSelectedEvent) {
            this.moduleSelectedListeners.forEach((listener) => {
                listener(event);
            });
        }
    }
}