import '../../../../../../api.ts';
import {PageTemplateSelector} from './PageTemplateSelector';

export class PageTemplateForm extends api.ui.form.Form {

    constructor(templateSelector: PageTemplateSelector) {
        super('page-template-form');

        let fieldSet = new api.ui.form.Fieldset();
        fieldSet.add(new api.ui.form.FormItemBuilder(templateSelector).setLabel('Page Template').build());
        this.add(fieldSet);
    }
}
