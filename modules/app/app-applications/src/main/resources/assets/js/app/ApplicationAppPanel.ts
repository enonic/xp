import '../api.ts';
import {ApplicationBrowsePanel} from './browse/ApplicationBrowsePanel';
import Application = api.application.Application;

export class ApplicationAppPanel extends api.app.AppPanel<Application> {

    constructor(path?: api.rest.Path) {

        super();

        this.route(path);
    }

    private route(path?: api.rest.Path) {
        let action = path ? path.getElement(0) : undefined;
        let id;

        switch (action) {
        case 'edit':
            id = path.getElement(1);
            if (id) {
                //TODO
            }
            break;
        case 'view' :
            id = path.getElement(1);
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
