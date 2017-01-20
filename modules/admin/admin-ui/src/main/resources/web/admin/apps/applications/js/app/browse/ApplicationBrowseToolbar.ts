import '../../api.ts';
import {ApplicationBrowseActions} from './ApplicationBrowseActions';

export class ApplicationBrowseToolbar extends api.ui.toolbar.Toolbar {

    constructor(actions: ApplicationBrowseActions) {
        super();
        super.addAction(actions.INSTALL_APPLICATION);
        super.addAction(actions.UNINSTALL_APPLICATION);
        super.addAction(actions.START_APPLICATION);
        super.addAction(actions.STOP_APPLICATION);
    }
}
