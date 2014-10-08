module api.module.inputtype.moduleconfigurator {

    import AEl = api.dom.AEl;
    import RootDataSet = api.data.RootDataSet;
    import Option = api.ui.selector.Option;
    import Value = api.data.Value;
    import FormView = api.form.FormView;
    import FormContextBuilder = api.form.FormContextBuilder;
    import Module = api.module.Module;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;

    export class ModuleConfigBuilder {

        moduleKey: string;

        config: RootDataSet;

        constructor() {
            this.moduleKey = "";
            this.config = new RootDataSet();
        }

        setModuleKey(moduleKey: string): ModuleConfigBuilder {
            this.moduleKey = moduleKey;
            return this;
        }

        setConfig(config: RootDataSet): ModuleConfigBuilder {
            this.config = config;
            return this;
        }

        build(): ModuleConfig {
            return new ModuleConfig(this);
        }

    }

    export class ModuleConfig extends api.dom.DivEl {

        private comboBox: ModuleComboBox;

        private selectedOption: Option<Module>;

        private selectedModule: Module;

        private formView: FormView;

        private moduleView: api.dom.DivEl;

        private configData: RootDataSet;

        private moduleSelectedListeners: {(event: ModuleSelectedEvent): void;}[];

        constructor(builder: ModuleConfigBuilder) {
            super("module-config");

            this.moduleSelectedListeners = [];

            this.comboBox = new ModuleComboBox(1);
            this.comboBox.onOptionSelected((event: OptionSelectedEvent<Module>) => {
                this.selectedOption = event.getOption();
                this.selectedModule = event.getOption().displayValue;
                this.addFormView();
            });

            if (!!builder.moduleKey) {
                this.comboBox.getLoader().onLoadedData((event:LoadedDataEvent<Module>) => {
                    var modules:Module[] = event.getData();
                    for (var i = 0; i < modules.length; i++) {
                        if (modules[i].getId() === builder.moduleKey) {
                            this.selectedModule = modules[i];
                            this.configData = builder.config;
                            builder.moduleKey = "";
                            this.comboBox.select(this.selectedModule);
                        }
                    }
                });
            }

            this.comboBox.onSelectedOptionRemoved(() => {
                this.removeFormView();
            });

            this.appendChild(this.comboBox);
        }

        addFormView() {
            if (!!this.selectedModule) {
                this.comboBox.hide();

                if (this.moduleView) {
                    this.removeChild(this.moduleView);
                    this.moduleView = null;
                }

                var formContext = new FormContextBuilder().build();
                this.configData = this.configData || new RootDataSet();
                var configForm = this.selectedModule.getForm();

                this.moduleView = new api.dom.DivEl("module-view");

                var namesAndIconView: api.app.NamesAndIconView;
                namesAndIconView = new api.app.NamesAndIconView(new api.app.NamesAndIconViewBuilder().
                    setSize(api.app.NamesAndIconViewSize.large)).
                    setMainName(this.selectedModule.getDisplayName()).
                    setSubName(this.selectedModule.getName() + "-" + this.selectedModule.getVersion()).
                    setIconClass("icon-xlarge icon-puzzle");

                var header = new api.dom.DivEl('header');
                header.appendChild(namesAndIconView);

                this.moduleView.appendChild(header);

                this.formView = new FormView(formContext, configForm, this.configData);
                this.formView.addClass("module-form");
                this.moduleView.appendChild(this.formView);

                this.appendChild(this.moduleView);

                var removeButton = new api.dom.AEl("remove-button remove-module");
                header.appendChild(removeButton);
                removeButton.onClicked((event: MouseEvent) => {
                    this.comboBox.deselect(this.selectedOption);
                });

                this.notifyOptionSelected(new ModuleSelectedEvent(this.selectedModule, this.formView));
            }
        }

        removeFormView() {
            this.comboBox.show();

            if (this.moduleView) {
                this.removeChild(this.moduleView);
                this.moduleView = null;
            }

            this.configData = null;

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