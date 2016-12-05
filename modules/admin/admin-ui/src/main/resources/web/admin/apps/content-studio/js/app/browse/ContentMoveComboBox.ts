import "../../api.ts";

import SelectedOption = api.ui.selector.combobox.SelectedOption;
import MoveContentSummaryLoader = api.content.resource.MoveContentSummaryLoader;
import ContentSummary = api.content.ContentSummary;
import ContentSelectedOptionsView = api.content.ContentSelectedOptionsView;
import ContentPath = api.content.ContentPath;
import SelectedOptionsView = api.ui.selector.combobox.SelectedOptionsView;

export class ContentMoveComboBox extends api.ui.selector.combobox.RichComboBox<ContentSummary> {

    protected loader: MoveContentSummaryLoader;

    constructor() {
        var richComboBoxBuilder: api.ui.selector.combobox.RichComboBoxBuilder<ContentSummary> = new api.ui.selector.combobox.RichComboBoxBuilder<ContentSummary>();
        richComboBoxBuilder
            .setMaximumOccurrences(1)
            .setComboBoxName("contentSelector")
            .setLoader(new MoveContentSummaryLoader())
            .setSelectedOptionsView(<SelectedOptionsView<ContentSummary>>new ContentSelectedOptionsView())
            .setOptionDisplayValueViewer(new api.content.ContentSummaryViewer())
            .setDelayedInputValueChangedHandling(500)
            .setSkipAutoDropShowOnValueChange(true);

        super(richComboBoxBuilder);
    }

    getLoader(): MoveContentSummaryLoader {
        return this.loader;
    }

    setFilterContentPaths(contentPaths: ContentPath[]) {
        this.getLoader().setFilterContentPaths(contentPaths);
    }

    setFilterContentTypes(contentTypes: api.schema.content.ContentType[]) {
        this.getLoader().setFilterContentTypes(contentTypes);
    }

    clearCombobox() {
        super.clearCombobox();
        this.getLoader().resetSearchString();
    }
}