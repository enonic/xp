module app_wizard {

    export class ModuleView extends api_dom.DivEl {

        private siteModule: api_module.Module;

        private moduleConfig: api_content_site.ModuleConfig;

        private formView: api_form.FormView;

        constructor(context: api_form.FormContext, theModule: api_module.Module, moduleConfig?: api_content_site.ModuleConfig) {
            super("ModuleView", "module-view");
            this.siteModule = theModule;
            this.moduleConfig = moduleConfig;

            var h4 = new api_dom.H4El();
            h4.getEl().setInnerHtml(theModule.getDisplayName());
            this.appendChild(h4);

            this.formView = new api_form.FormView(context, theModule.getForm(), moduleConfig ? moduleConfig.getConfig() : undefined);
            this.appendChild(this.formView);
        }

        getModuleConfig(): api_content_site.ModuleConfig {
            var config = this.formView.rebuildContentData();
            this.moduleConfig.setConfig(config);
            return this.moduleConfig;
        }
    }

}