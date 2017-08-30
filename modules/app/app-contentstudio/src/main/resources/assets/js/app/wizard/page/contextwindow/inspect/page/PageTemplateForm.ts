import '../../../../../../api.ts';
import {PageTemplateSelector} from './PageTemplateSelector';
import i18n = api.util.i18n;

export class PageTemplateForm extends api.ui.form.Form {

    constructor(templateSelector: PageTemplateSelector) {
        super('page-template-form');

        let fieldSet = new api.ui.form.Fieldset();
        fieldSet.add(new api.ui.form.FormItemBuilder(templateSelector).setLabel(i18n('field.page.template')).build());
        this.add(fieldSet);
    }
}
