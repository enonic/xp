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
        this.contentLoader.setSize(-1);
        var richComboBoxBuilder: api.ui.selector.combobox.RichComboBoxBuilder<ContentSummary> = new api.ui.selector.combobox.RichComboBoxBuilder<ContentSummary>();
        richComboBoxBuilder
            .setMaximumOccurrences(1)
            .setComboBoxName("contentSelector")
            .setLoader(this.contentLoader)
            .setSelectedOptionsView(new ContentSelectedOptionsView())
            .setOptionDisplayValueViewer(new api.content.ContentSummaryViewer())
            .setDelayedInputValueChangedHandling(500);

        super(richComboBoxBuilder);
    }

    setFilterContentPath(contentPath: ContentPath) {
        this.contentLoader.setFilterContentPath(contentPath);
    }

    setFilterSourceContentType(contentType: api.schema.content.ContentType) {
        this.contentLoader.setFilterSourceContentType(contentType);
    }

    clearCombobox() {
        super.clearCombobox();
        this.contentLoader.resetSearchString();
    }
}