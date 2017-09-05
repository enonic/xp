import '../../../../../../api.ts';

import ContentComboBox = api.content.ContentComboBox;
import ImageContentComboBox = api.content.image.ImageContentComboBox;

export class ImageSelectorForm extends api.ui.form.Form {

    private imageSelector: ImageContentComboBox;

    constructor(templateSelector: ImageContentComboBox, title?: string) {
        super('image-combobox-form');
        this.imageSelector = templateSelector;

        let fieldSet = new api.ui.form.Fieldset();
        if (!api.util.StringHelper.isBlank(title)) {
            fieldSet.add(new api.ui.form.FormItemBuilder(templateSelector).setLabel(title).build());
        } else {
            fieldSet.add(new api.ui.form.FormItemBuilder(templateSelector).build());
        }

        this.add(fieldSet);
    }

    getSelector(): ImageContentComboBox {
        return this.imageSelector;
    }

}
