module app {

    export class ModuleAppPanel extends api.app.BrowseAndWizardBasedAppPanel<api.module.Module> {

        constructor(appBar: api.app.bar.AppBar, path?: api.rest.Path) {

            super({
                appBar: appBar
            });

            this.handleGlobalEvents();

            this.route(path)
        }

        private route(path?: api.rest.Path) {
            var action = path ? path.getElement(0) : undefined;

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

            api.app.bar.event.ShowBrowsePanelEvent.on((event) => {
                this.handleBrowse(event);
            });
        }

        private handleBrowse(event: api.app.bar.event.ShowBrowsePanelEvent) {
            var browsePanel: api.app.browse.BrowsePanel<api.module.Module> = this.getBrowsePanel();
            if (!browsePanel) {
                this.addBrowsePanel(new app.browse.ModuleBrowsePanel());
            } else {
                this.showPanel(browsePanel);
            }
        }
    }
}