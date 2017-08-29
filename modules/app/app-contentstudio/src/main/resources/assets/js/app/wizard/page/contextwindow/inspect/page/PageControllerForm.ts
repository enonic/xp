import '../../../../../../api.ts';
import {PageControllerSelector} from './PageControllerSelector';
import i18n = api.util.i18n;

export class PageControllerForm extends api.ui.form.Form {

    constructor(controllerSelector: PageControllerSelector) {
        super('page-controller-form');

        let fieldSet = new api.ui.form.Fieldset();
        fieldSet.add(new api.ui.form.FormItemBuilder(controllerSelector).setLabel(i18n('field.page.controller')).build());
        this.add(fieldSet);
    }

}
