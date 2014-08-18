module app.wizard.site {

    import FormValidityChangedEvent = api.form.FormValidityChangedEvent;

    export class ModuleView extends api.dom.DivEl {

        private siteModule: api.module.Module;

        private moduleConfig: api.content.site.ModuleConfig;

        private formView: api.form.FormView;

        constructor(context: api.content.form.ContentFormContext, theModule: api.module.Module,
                    moduleConfig?: api.content.site.ModuleConfig) {
            super("module-view");
            this.siteModule = theModule;
            this.moduleConfig = moduleConfig;

            var namesAndIconView: api.app.NamesAndIconView;
            namesAndIconView = new api.app.NamesAndIconView(new api.app.NamesAndIconViewBuilder().
                setSize(api.app.NamesAndIconViewSize.large)).
                setMainName(theModule.getDisplayName()).
                setSubName(theModule.getName() + "-" + theModule.getVersion()).
                setIconClass("icon-xlarge icon-puzzle");

            var linkEl = new api.dom.AEl("reset");
            linkEl.setHtml("reset");
            linkEl.onClicked((event: MouseEvent) => {
                this.formView.resetDataSet();
                return false;
            });
            var header = new api.dom.DivEl('header');
            header.appendChild(namesAndIconView);
            header.appendChild(linkEl);

            this.appendChild(header);

            this.formView = new api.form.FormView(context, theModule.getForm(), moduleConfig ? moduleConfig.getConfig() : undefined);
            this.formView.setDoOffset(false);
            this.appendChild(this.formView);
        }

        getModuleConfig(): api.content.site.ModuleConfig {
            var config = this.formView.getData();
            this.moduleConfig.setConfig(config);
            return this.moduleConfig;
        }

        onFocus(listener: (event: FocusEvent) => void) {
            this.formView.onFocus(listener);
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.formView.unFocus(listener);
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.formView.onBlur(listener);
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.formView.unBlur(listener);
        }

        onValidityChanged(listener: (event: FormValidityChangedEvent)=>void) {
            this.formView.validate();
            this.formView.onValidityChanged(listener);
        }

        unValidityChanged(listener: (event: FormValidityChangedEvent)=>void) {
            this.unValidityChanged(listener);
        }

        onEditContentRequest(listener: (content: api.content.ContentSummary) => void) {
            this.formView.onEditContentRequest(listener);
        }

        unEditContentRequest(listener: (content: api.content.ContentSummary) => void) {
            this.formView.unEditContentRequest(listener);
        }

    }

}