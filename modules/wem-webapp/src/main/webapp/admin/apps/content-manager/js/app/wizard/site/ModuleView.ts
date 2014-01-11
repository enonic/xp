module app.wizard.site {

    export class ModuleView extends api.dom.DivEl {

        private siteModule: api.module.Module;

        private moduleConfig: api.content.site.ModuleConfig;

        private formView: api.form.FormView;

        constructor(context: api.form.FormContext, theModule: api.module.Module, moduleConfig?: api.content.site.ModuleConfig) {
            super(true, "module-view");
            this.siteModule = theModule;
            this.moduleConfig = moduleConfig;

            var h4 = new api.dom.H4El();
            h4.getEl().setInnerHtml(theModule.getDisplayName());
            this.appendChild(h4);

            this.formView = new api.form.FormView(context, theModule.getForm(), moduleConfig ? moduleConfig.getConfig() : undefined);
            this.appendChild(this.formView);
        }

        getModuleConfig(): api.content.site.ModuleConfig {
            var config = this.formView.rebuildContentData();
            this.moduleConfig.setConfig(config);
            return this.moduleConfig;
        }
    }

}