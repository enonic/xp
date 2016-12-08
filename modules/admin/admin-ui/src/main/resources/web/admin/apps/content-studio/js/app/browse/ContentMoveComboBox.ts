import "../../api.ts";

import SelectedOption = api.ui.selector.combobox.SelectedOption;
import MoveContentSummaryLoader = api.content.resource.MoveContentSummaryLoader;
import ContentSummary = api.content.ContentSummary;
import ContentSelectedOptionsView = api.content.ContentSelectedOptionsView;
import ContentPath = api.content.ContentPath;

export class ContentMoveComboBox extends api.ui.selector.combobox.RichComboBox<ContentSummary> {

    contentLoader: MoveContentSummaryLoader;

    constructor() {
        this.contentLoader = new MoveContentSummaryLoader();
        var richComboBoxBuilder: api.ui.selector.combobox.RichComboBoxBuilder<ContentSummary> = new api.ui.selector.combobox.RichComboBoxBuilder<ContentSummary>();
        richComboBoxBuilder
            .setMaximumOccurrences(1)
            .setComboBoxName("contentSelector")
            .setLoader(this.contentLoader)
            .setSelectedOptionsView(new ContentSelectedOptionsView())
            .setOptionDisplayValueViewer(new api.content.ContentSummaryViewer())
            .setDelayedInputValueChangedHandling(500)
            .setSkipAutoDropShowOnValueChange(true);

        super(richComboBoxBuilder);
    }

    setFilterContentPaths(contentPaths: ContentPath[]) {
        this.contentLoader.setFilterContentPaths(contentPaths);
    }

    setFilterContentTypes(contentTypes: api.schema.content.ContentType[]) {
        this.contentLoader.setFilterContentTypes(contentTypes);
    }

    clearCombobox() {
        super.clearCombobox();
        this.getComboBox().getComboBoxDropdownGrid().removeAllOptions();
        this.contentLoader.resetSearchString();
    }
}