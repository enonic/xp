module app_wizard {

    export class SiteWizardStepForm extends api_app_wizard.WizardStepForm {

        private formContext: api_form.FormContext;

        private moduleConfigsByKey: api_content_site.ModuleConfig[];

        private moduleViewsCtr: api_dom.DivEl;

        private templateViewCtr: api_dom.DivEl;

        constructor() {
            super("SiteWizardStepForm");

            this.templateViewCtr = new api_dom.DivEl();
            this.appendChild(this.templateViewCtr);
            this.moduleViewsCtr = new api_dom.DivEl();
            this.appendChild(this.moduleViewsCtr);
        }

        public renderNew() {
            //TODO
        }

        public renderExisting(context: api_form.FormContext, site: api_content_site.Site) {
            this.formContext = context;

            this.moduleConfigsByKey = [];
            var moduleConfigs: api_content_site.ModuleConfig[] = site.getModuleConfigs();
            for (var i = 0; i < moduleConfigs.length; i++) {
                this.moduleConfigsByKey[moduleConfigs[i].getModuleKey().toString()] = moduleConfigs[i];
            }

            this.loadModules(site, (modules: api_module.Module[]) => {
                this.layoutModules(modules);
            });

            this.loadSiteTemplate(site, (template: api_content_site_template.SiteTemplate) => {
                this.layoutSiteTemplate(template);
            });
        }

        public getTemplateKey(): api_content_site_template.SiteTemplateKey {
            return (<TemplateView>this.templateViewCtr.getFirstChild()).getSiteTemplateKey();
        }

        public getModuleConfigs(): api_content_site.ModuleConfig[] {
            var moduleConfigs: api_content_site.ModuleConfig[] = [];
            var moduleViews = this.moduleViewsCtr.getChildren();
            for (var i = 0; i < moduleViews.length; i++) {
                moduleConfigs.push((<ModuleView>moduleViews[i]).getModuleConfig());
            }
            return moduleConfigs;
        }

        private loadModules(site: api_content_site.Site, callback: (siteModules: api_module.Module[]) => void) {
            var moduleConfigs: api_content_site.ModuleConfig[] = site.getModuleConfigs();
            var moduleRequests = [];
            for (var i = 0; i < moduleConfigs.length; i++) {
                var request = new api_module.GetModuleRequest(moduleConfigs[i].getModuleKey()).send();
                moduleRequests.push(request);
            }
            // Using .apply() here to pass array of requests as arguments enum
            jQuery.when.apply(jQuery, moduleRequests).then((moduleResponses: api_rest.JsonResponse<api_module_json.ModuleJson>[]) => {
                var modules = [];
                // Make array in case there's only one response
                moduleResponses = [].concat(moduleResponses);
                for (var i = 0; i < moduleResponses.length; i++) {
                    modules.push(new api_module.Module(moduleResponses[i].getResult()));
                }
                callback(modules);
            })
        }

        private layoutModules(modules: api_module.Module[]) {
            this.moduleViewsCtr.removeChildren();
            modules.forEach((theModule: api_module.Module) => {
                var moduleView = new ModuleView(this.formContext, theModule, this.moduleConfigsByKey[theModule.getModuleKey().toString()]);
                this.moduleViewsCtr.appendChild(moduleView)
            });
        }

        private loadSiteTemplate(site: api_content_site.Site, callback: (siteTemplate: api_content_site_template.SiteTemplate) => void) {
            new api_content_site_template.GetSiteTemplateRequest(site.getTemplateKey()).send()
                .done((templateResponse: api_rest.JsonResponse<api_content_site_template_json.SiteTemplateJson>) => {
                    var template = new api_content_site_template.SiteTemplate(templateResponse.getResult());
                    callback(template);
                }
            );
        }

        private layoutSiteTemplate(template: api_content_site_template.SiteTemplate) {
            this.templateViewCtr.removeChildren();
            this.templateViewCtr.appendChild(new TemplateView(template))
        }

    }

    export class ModuleView extends api_form.FormView {

        private siteModule: api_module.Module;

        private moduleConfig: api_content_site.ModuleConfig;

        constructor(context: api_form.FormContext, theModule: api_module.Module, moduleConfig?: api_content_site.ModuleConfig) {
            super(context, theModule.getForm(), moduleConfig ? moduleConfig.getConfig() : undefined);
            this.siteModule = theModule;
            this.moduleConfig = moduleConfig;
            var h4 = new api_dom.H4El();
            h4.getEl().setInnerHtml(theModule.getDisplayName());
            this.prependChild(h4);
        }

        getModuleConfig(): api_content_site.ModuleConfig {
            var config = this.rebuildContentData();
            this.moduleConfig.setConfig(config);
            return this.moduleConfig;
        }
    }

    export class TemplateView extends api_dom.DivEl {

        private siteTemplate: api_content_site_template.SiteTemplate;

        constructor(template: api_content_site_template.SiteTemplate) {
            super("TemplateView");
            this.siteTemplate = template;
            var h4 = new api_dom.H4El();
            var p = new api_dom.PEl();
            h4.getEl().setInnerHtml(template.getDisplayName());
            p.getEl().setInnerHtml(template.getDescription());
            this.appendChild(h4);
            this.appendChild(p);
        }

        getSiteTemplateKey(): api_content_site_template.SiteTemplateKey {
            return this.siteTemplate.getKey();
        }
    }

}
