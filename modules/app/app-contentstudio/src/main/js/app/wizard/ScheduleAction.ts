import '../../api.ts';
import i18n = api.util.i18n;

export class ScheduleAction extends api.ui.Action {
    constructor() {
        super(i18n('action.schedule'));
    }
}
