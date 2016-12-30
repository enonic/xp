import "../api.ts";
import {ApplicationBrowsePanel} from "./browse/ApplicationBrowsePanel";
import Application = api.application.Application;

export class ApplicationAppPanel extends api.app.AppPanel<Application> {

    constructor(path?: api.rest.Path) {

        super();

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
            new api.app.ShowBrowsePanelEvent().fire();
            break;
        }
    }

    protected createBrowsePanel() {
        return new ApplicationBrowsePanel();
    }
}
