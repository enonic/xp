module app {

    export class TemplateAppPanel extends api.app.BrowseAndWizardBasedAppPanel<app.browse.TemplateBrowseItem> {

        private browseActions: app.browse.action.TemplateBrowseActions;

        private templateTreeGrid: app.browse.TemplateTreeGrid;

        constructor(appBar: api.app.bar.AppBar, path?: api.rest.Path) {

            super({
                appBar: appBar
            });

            this.browseActions = new app.browse.action.TemplateBrowseActions();
            this.templateTreeGrid = new app.browse.TemplateTreeGrid(this.browseActions);
            this.handleGlobalEvents();

            this.route(path);

        }

        private route(path: api.rest.Path) {
            var action = path.getElement(0);

            switch (action) {
            case 'edit':
                var id = path.getElement(1);
                if (id) {
                    //TODO
                }
                break;
            case 'view' :
                var id = path.getElement(1);
                if (id) {
                    //TODO
                }
                break;
            default:
                new api.app.bar.event.ShowBrowsePanelEvent().fire();
                break;
            }
        }

        private handleGlobalEvents() {

            var templateUploader = new app.imp.TemplateUploader();
            var dialog = new api.ui.dialog.UploadDialog(
                "Template Importer", "Templates will be imported in application", templateUploader
            );
            templateUploader.onFinishUpload((response: api.content.site.template.InstallSiteTemplateResponse) => {
                var templates = response.getSiteTemplates();
                if (templates.length > 0) {
                    api.notify.showFeedback('Template \'' + templates.map((template: api.content.site.template.SiteTemplateSummary) => {
                        console.log(template);
                        return template.getDisplayName()
                    }).join(', ') + '\' was installed');
                }
                new api.content.site.template.SiteTemplateImportedEvent().fire();
                dialog.close();
            });
            templateUploader.onError((resp: api.rest.RequestError) => {
                api.notify.showError("Invalid Template file");
                dialog.close();
            });

            app.browse.event.ImportTemplateEvent.on(() => {
                dialog.open();
            });

            app.browse.event.ExportTemplateEvent.on((event: app.browse.event.ExportTemplateEvent) => {
                var template: app.browse.TemplateSummary = event.getTemplate();

                var exportTemplate = new api.content.site.template.ExportSiteTemplateRequest(template.getSiteTemplateKey());
                var templateExportUrl = exportTemplate.getRequestPath().toString() + '?siteTemplateKey=' +
                                        template.getSiteTemplateKey().toString();

                window.location.href = templateExportUrl;
            });

            app.browse.event.NewTemplateEvent.on((event: app.browse.event.NewTemplateEvent) => {
                this.handleNew(event);
            });

            app.browse.event.EditTemplateEvent.on((event: app.browse.event.EditTemplateEvent) => {
                this.handleEdit(event);
            });

            api.app.bar.event.ShowBrowsePanelEvent.on((event: api.app.bar.event.ShowBrowsePanelEvent) => {
                this.handleBrowse(event);
            })
        }

        private handleBrowse(event: api.app.bar.event.ShowBrowsePanelEvent) {
            var browsePanel: api.app.browse.BrowsePanel<app.browse.TemplateBrowseItem> = this.getBrowsePanel();
            if (!browsePanel) {
                this.addBrowsePanel(new app.browse.TemplateBrowsePanel(this.browseActions, this.templateTreeGrid));
            } else {
                this.showPanel(browsePanel);
            }
        }

        private handleNew(event: app.browse.event.NewTemplateEvent) {
            var tabId = api.app.bar.AppBarTabId.forNew('new-site-template-wizard');
            var tabMenuItem = new api.app.bar.AppBarTabMenuItem("[New Site Template]", tabId);
            var wizard = new app.wizard.SiteTemplateWizardPanel(tabId);
            this.addWizardPanel(tabMenuItem, wizard);
        }

        private handleEdit(event) {
            event.getTemplates().forEach((template: app.browse.TemplateSummary) => {

                if (!template.isSiteTemplate()) {
                    return;
                }
                new api.content.site.template.GetSiteTemplateRequest(template.getSiteTemplateKey()).sendAndParse().
                    done((siteTemplate: api.content.site.template.SiteTemplate)=> {

                        var tabId = api.app.bar.AppBarTabId.forEdit(template.getId());
                        var tabMenuItem = new api.app.bar.AppBarTabMenuItem(siteTemplate.getDisplayName(), tabId);
                        var wizard = new app.wizard.SiteTemplateWizardPanel(tabId, siteTemplate);
                        this.addWizardPanel(tabMenuItem, wizard);
                    });

            });
        }

    }
}
