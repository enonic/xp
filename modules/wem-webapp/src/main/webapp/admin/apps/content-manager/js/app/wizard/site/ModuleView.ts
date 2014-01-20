module app.wizard.site {

    export class ModuleView extends api.dom.DivEl {

        private siteModule: api.module.Module;

        private moduleConfig: api.content.site.ModuleConfig;

        private formView: api.form.FormView;

        constructor(context: api.form.FormContext, theModule: api.module.Module, moduleConfig?: api.content.site.ModuleConfig) {
            super("module-view");
            this.siteModule = theModule;
            this.moduleConfig = moduleConfig;

            var headerEl = new api.dom.H3El();
            headerEl.setText(theModule.getDisplayName());
            this.appendChild(headerEl);

            this.formView = new api.form.FormView(context, theModule.getForm(), moduleConfig ? moduleConfig.getConfig() : undefined);
            this.appendChild(this.formView);
        }

        getModuleConfig(): api.content.site.ModuleConfig {
            var config = this.formView.getData();
            this.moduleConfig.setConfig(config);
            return this.moduleConfig;
        }
    }

}