import "../../../../../../api.ts";

import PropertyChangedEvent = api.PropertyChangedEvent;
import LiveEditModel = api.liveedit.LiveEditModel;
import PageModel = api.content.page.PageModel;
import PageDescriptor = api.content.page.PageDescriptor;
import DescriptorKey = api.content.page.DescriptorKey;
import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;

export class PageControllerSelector extends api.content.page.PageDescriptorDropdown {

    private pageModel: PageModel;

    constructor(model: LiveEditModel) {
        super(model);

        this.pageModel = model.getPageModel();

        this.onLoadedData((event: LoadedDataEvent<PageDescriptor>) => {

            if (this.pageModel.hasController() && this.pageModel.getController().getKey().toString() !== this.getValue()) {
                this.selectController(this.pageModel.getController().getKey());
            }
        });

        this.pageModel.onPropertyChanged((event: PropertyChangedEvent) => {
            if (event.getPropertyName() == PageModel.PROPERTY_CONTROLLER && this !== event.getSource()) {
                let descriptorKey = <DescriptorKey>event.getNewValue();
                if (descriptorKey) {
                    this.selectController(descriptorKey);
                }
                // TODO: Change class to extend a PageDescriptorComboBox instead, since we then can deselect.
            }
        });
    }

    private selectController(descriptorKey: DescriptorKey) {

        let optionToSelect = this.getOptionByValue(descriptorKey.toString());
        if (optionToSelect) {
            this.selectOption(optionToSelect, true);
        }
    }
}
