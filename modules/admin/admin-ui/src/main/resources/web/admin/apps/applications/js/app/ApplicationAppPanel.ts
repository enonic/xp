import "../api.ts";
import {ApplicationBrowsePanel} from "./browse/ApplicationBrowsePanel";
import Application = api.application.Application;

export class ApplicationAppPanel extends api.app.BrowseAndWizardBasedAppPanel<Application> {

    constructor(appBar: api.app.bar.AppBar, path?: api.rest.Path) {

        super({
            appBar: appBar
        });

        this.handleGlobalEvents();

        this.route(path);
    }

    private route(path?: api.rest.Path) {
        const action = path ? path.getElement(0) : null;
        const id = path ? path.getElement(1) : null;

        switch (action) {
        case 'edit':
            if (id) {
                // TODO
            }
            break;
        case 'view' :
            if (id) {
                // TODO
            }
            break;
        default:
            new api.app.ShowBrowsePanelEvent().fire();
            break;
        }
    }

    private handleGlobalEvents() {

        api.app.ShowBrowsePanelEvent.on((event) => {
            this.handleBrowse(event);
        });
    }

    private handleBrowse(event: api.app.ShowBrowsePanelEvent) {
        let browsePanel: api.app.browse.BrowsePanel<Application> = this.getBrowsePanel();
        if (!browsePanel) {
            this.addBrowsePanel(new ApplicationBrowsePanel());
        } else {
            this.showPanel(browsePanel);
        }
    }
}
