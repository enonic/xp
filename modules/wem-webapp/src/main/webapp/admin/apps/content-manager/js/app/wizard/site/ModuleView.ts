module app.wizard.site {

    export class ModuleView extends api.dom.DivEl {

        private siteModule: api.module.Module;

        private moduleConfig: api.content.site.ModuleConfig;

        private formView: api.form.FormView;

        constructor(context: api.form.FormContext, theModule: api.module.Module, moduleConfig?: api.content.site.ModuleConfig) {
            super("module-view");
            this.siteModule = theModule;
            this.moduleConfig = moduleConfig;

            var header = new api.dom.DivEl('header');
            header.appendChild(new api.dom.ImgEl(api.util.getAdminUri('common/images/icons/icoMoon/32x32/puzzle.png')));
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
    }

}