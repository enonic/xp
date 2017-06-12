import '../../../../../../api.ts';

import PropertyChangedEvent = api.PropertyChangedEvent;
import LiveEditModel = api.liveedit.LiveEditModel;
import PageModel = api.content.page.PageModel;
import PageDescriptor = api.content.page.PageDescriptor;
import DescriptorKey = api.content.page.DescriptorKey;
import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;

export class PageControllerSelector extends api.content.page.PageDescriptorDropdown {

    constructor(model: LiveEditModel) {
        super(model);

        const pageModel: PageModel = model.getPageModel();

        this.onLoadedData((event: LoadedDataEvent<PageDescriptor>) => {

            if (pageModel.hasController() && pageModel.getController().getKey().toString() !== this.getValue()) {
                this.selectController(pageModel.getController().getKey());
            }
        });

        pageModel.onPropertyChanged((event: PropertyChangedEvent) => {
            if (event.getPropertyName() === PageModel.PROPERTY_CONTROLLER && this !== event.getSource()) {
                let descriptorKey = <DescriptorKey>event.getNewValue();
                if (descriptorKey) {
                    this.selectController(descriptorKey);
                }
                // TODO: Change class to extend a PageDescriptorComboBox instead, since we then can deselect.
            }
        });

        pageModel.onReset(() => {
            this.reset();
        });

        this.load();
    }

    protected handleOptionSelected(event: api.ui.selector.OptionSelectedEvent<api.content.page.PageDescriptor>) {
        new api.ui.dialog.ConfirmationDialog()
            .setQuestion(
                'Changing a page controller will result in losing changes made to the page. Are you sure?')
            .setNoCallback(() => {
                this.selectOption(event.getPreviousOption(), true); // reverting selection back
                this.resetActiveSelection();
            })
            .setYesCallback(() => {
                super.handleOptionSelected(event);
            }).open();
    }

    private selectController(descriptorKey: DescriptorKey) {

        let optionToSelect = this.getOptionByValue(descriptorKey.toString());
        if (optionToSelect) {
            this.selectOption(optionToSelect, true);
        }
    }
}
