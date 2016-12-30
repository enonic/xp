import "../../../../../../api.ts";
import {PageControllerSelector} from "./PageControllerSelector";

export class PageControllerForm extends api.ui.form.Form {

    private controllerSelector: PageControllerSelector;

    constructor(controllerSelector: PageControllerSelector) {
        super('page-controller-form');
        this.controllerSelector = controllerSelector;

        let fieldSet = new api.ui.form.Fieldset();
        fieldSet.add(new api.ui.form.FormItemBuilder(controllerSelector).setLabel("Page Controller").build());
        this.add(fieldSet);
    }

    getSelector(): PageControllerSelector {
        return this.controllerSelector;
    }

}
