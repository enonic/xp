import '../../../../../api.ts';
import i18n = api.util.i18n;

export class NoSelectionInspectionPanel extends api.ui.panel.Panel {

    private header: api.app.NamesView;

    constructor() {
        super('inspection-panel');

        this.header = new api.app.NamesView().setMainName(i18n('field.inspection.empty'));

        this.appendChild(this.header);
    }
}
