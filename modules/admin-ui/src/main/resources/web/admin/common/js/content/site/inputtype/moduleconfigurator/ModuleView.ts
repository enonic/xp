module api.content.site.inputtype.moduleconfigurator {

    import AEl = api.dom.AEl;
    import PropertyTree = api.data.PropertyTree;
    import PropertySet = api.data.PropertySet;
    import Option = api.ui.selector.Option;
    import FormView = api.form.FormView;
    import FormContextBuilder = api.form.FormContextBuilder;
    import Module = api.module.Module;
    import ModuleKey = api.module.ModuleKey;
    import ModuleConfig = api.content.site.ModuleConfig;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;

    export class ModuleView extends api.dom.DivEl {

        private module: Module;

        private formView: FormView;

        private moduleConfig: ModuleConfig;

        private removeClickedListeners: {(event: MouseEvent): void;}[];

        private collapseClickedListeners: {(event: MouseEvent): void;}[];

        private moduleConfigFormDisplayedListeners: {(moduleKey: ModuleKey) : void}[] = [];

        constructor(mod: Module, moduleConfig: ModuleConfig, formContext: api.content.form.ContentFormContext) {
            super("module-view");

            this.removeClickedListeners = [];
            this.collapseClickedListeners = [];

            this.module = mod;
            this.moduleConfig = moduleConfig;

            var header = new api.dom.DivEl('header');

            var namesAndIconView = new api.app.NamesAndIconView(new api.app.NamesAndIconViewBuilder().
                setSize(api.app.NamesAndIconViewSize.large)).
                setMainName(this.module.getDisplayName()).
                setSubName(this.module.getName() + "-" + this.module.getVersion()).
                setIconClass("icon-xlarge icon-puzzle");

            header.appendChild(namesAndIconView);

            var removeButton = new api.dom.AEl("remove-button icon-close");
            removeButton.onClicked((event: MouseEvent) => {
                this.notifyRemoveClicked(event);
            });
            header.appendChild(removeButton);

            var collapseButton = new api.dom.AEl('collapse-button');
            collapseButton.setHtml('Collapse');
            collapseButton.onClicked((event: MouseEvent) => {
                if (this.formView.isVisible()) {
                    this.formView.hide();
                    collapseButton.setHtml('Expand');
                    this.addClass('collapsed');
                } else {
                    this.formView.show();
                    collapseButton.setHtml('Collapse');
                    this.removeClass('collapsed');
                }
                this.notifyCollapseClicked(event);
            });
            header.appendChild(collapseButton);

            this.appendChild(header);

            this.formView = new FormView(formContext, this.module.getForm(), this.moduleConfig.getConfig());
            this.formView.addClass("module-form");
            this.appendChild(this.formView);
            this.formView.layout().then(() => {
                this.notifyModuleConfigFormDisplayed(this.module.getModuleKey());
                this.formView.onEditContentRequest((content: api.content.ContentSummary) => {
                    new api.content.EditContentEvent([content]).fire();
                });
            }).catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            }).done();
        }

        getModule(): Module {
            return this.module;
        }

        getModuleConfig(): ModuleConfig {
            return this.moduleConfig;
        }

        getFormView(): FormView {
            return this.formView;
        }

        onRemoveClicked(listener: (event: MouseEvent) => void) {
            this.removeClickedListeners.push(listener);
        }

        unRemoveClicked(listener: (event: MouseEvent) => void) {
            this.removeClickedListeners = this.removeClickedListeners.filter((curr) => {
                return listener != curr;
            })
        }

        private notifyRemoveClicked(event: MouseEvent) {
            this.removeClickedListeners.forEach((listener) => {
                listener(event);
            })
        }

        onCollapseClicked(listener: (event: MouseEvent) => void) {
            this.collapseClickedListeners.push(listener);
        }

        unCollapseClicked(listener: (event: MouseEvent) => void) {
            this.collapseClickedListeners = this.collapseClickedListeners.filter((curr) => {
                return listener != curr;
            })
        }

        private notifyCollapseClicked(event: MouseEvent) {
            this.collapseClickedListeners.forEach((listener) => {
                listener(event);
            })
        }

        onModuleConfigFormDisplayed(listener: {(moduleKey: ModuleKey): void;}) {
            this.moduleConfigFormDisplayedListeners.push(listener);
        }

        unModuleConfigFormDisplayed(listener: {(moduleKey: ModuleKey): void;}) {
            this.moduleConfigFormDisplayedListeners =
            this.moduleConfigFormDisplayedListeners.filter((curr) => (curr != listener));
        }

        private notifyModuleConfigFormDisplayed(moduleKey: ModuleKey) {
            this.moduleConfigFormDisplayedListeners.forEach((listener) => listener(moduleKey));
        }
    }
}