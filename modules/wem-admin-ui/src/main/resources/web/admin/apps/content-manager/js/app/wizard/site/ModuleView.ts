module app.wizard.site {

    import FormValidityChangedEvent = api.form.FormValidityChangedEvent;

    export class ModuleView extends api.dom.DivEl {

        private siteModule: api.module.Module;

        private moduleConfig: api.content.site.ModuleConfig;

        private formView: api.form.FormView;

        constructor(context: api.form.FormContext, theModule: api.module.Module, moduleConfig?: api.content.site.ModuleConfig) {
            super("module-view");
            this.siteModule = theModule;
            this.moduleConfig = moduleConfig;

            var header = new api.dom.DivEl('header');
            header.appendChild(new api.dom.IEl('icon-large icon-puzzle'));
            header.appendChild(new api.dom.H6El().setText(theModule.getDisplayName()));
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

    }

}