import '../../../../../api.ts';
import RequestError = api.rest.RequestError;

export class BaseInspectionPanel extends api.ui.panel.Panel {

    constructor() {
        super('inspection-panel');

        this.onRendered(() => {
            wemjq(this.getHTMLElement()).slimScroll({
                height: '100%'
            });
        });
    }

    isNotFoundError(reason: any): boolean {
        return reason instanceof RequestError && (<RequestError>reason).isNotFound();
    }
}
