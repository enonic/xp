import '../../api.ts';

import SelectedOption = api.ui.selector.combobox.SelectedOption;
import MoveContentSummaryLoader = api.content.resource.MoveContentSummaryLoader;
import ContentSummary = api.content.ContentSummary;
import ContentSelectedOptionsView = api.content.ContentSelectedOptionsView;
import ContentPath = api.content.ContentPath;
import SelectedOptionsView = api.ui.selector.combobox.SelectedOptionsView;
import RichComboBoxBuilder = api.ui.selector.combobox.RichComboBoxBuilder;
import ContentTypeName = api.schema.content.ContentTypeName;
import ContentComboBox = api.content.ContentComboBox;
import ContentComboBoxBuilder = api.content.ContentComboBoxBuilder;
import ContentSummaryOptionDataHelper = api.content.ContentSummaryOptionDataHelper;
import ContentSummaryOptionDataLoader = api.content.ContentSummaryOptionDataLoader;

export class ContentMoveComboBox extends ContentComboBox {

    private readonlyChecker: MoveReadOnlyChecker;

    constructor() {
        const richComboBoxBuilder: ContentComboBoxBuilder = new ContentComboBoxBuilder();

        richComboBoxBuilder
            .setMaximumOccurrences(1)
            .setComboBoxName('contentSelector')
            .setLoader(new MoveContentSummaryLoader())
            .setSelectedOptionsView(<SelectedOptionsView<ContentSummary>>new ContentSelectedOptionsView())
            .setOptionDisplayValueViewer(new api.content.ContentSummaryViewer())
            .setDelayedInputValueChangedHandling(500)
            .setSkipAutoDropShowOnValueChange(true)
            .setTreegridDropdownEnabled(true)
            .setOptionDataHelper(new ContentSummaryOptionDataHelper())
            .setOptionDataLoader(new ContentSummaryOptionDataLoader());

        super(richComboBoxBuilder);
        this.readonlyChecker = new MoveReadOnlyChecker();
        this.getComboBox().getComboBoxDropdownGrid().setReadonlyChecker(this.readonlyChecker.isReadOnly.bind(this.readonlyChecker));
        this.onOptionDeselected(() => {
            this.getComboBox().getInput().reset();
        });
    }

    getLoader(): MoveContentSummaryLoader {
        return <MoveContentSummaryLoader> this.loader;
    }

    setFilterContents(contents: ContentSummary[]) {
        this.getLoader().setFilterContentPaths(contents.map((content) => content.getPath()));
        this.readonlyChecker.setFilterContentPaths(contents.map((content) => content.getPath()));
    }

    setFilterContentTypes(contentTypes: api.schema.content.ContentType[]) {
        this.getLoader().setFilterContentTypes(contentTypes);
    }

    clearCombobox() {
        super.clearCombobox();
        this.getComboBox().getComboBoxDropdownGrid().removeAllOptions();
        this.getLoader().resetSearchString();
    }
}

class MoveReadOnlyChecker {

    private filterContentPaths: ContentPath[] = [];

    private filterContentTypes: ContentTypeName[] = [ContentTypeName.IMAGE, ContentTypeName.MEDIA, ContentTypeName.PAGE_TEMPLATE,
        ContentTypeName.FRAGMENT, ContentTypeName.MEDIA_DATA, ContentTypeName.MEDIA_AUDIO, ContentTypeName.MEDIA_ARCHIVE,
        ContentTypeName.MEDIA_VIDEO, ContentTypeName.MEDIA_CODE, ContentTypeName.MEDIA_EXECUTABLE, ContentTypeName.MEDIA_PRESENTATION,
        ContentTypeName.MEDIA_SPREADSHEET, ContentTypeName.MEDIA_UNKNOWN, ContentTypeName.MEDIA_DOCUMENT, ContentTypeName.MEDIA_VECTOR,
        ContentTypeName.SHORTCUT];

    isReadOnly(item: ContentSummary): boolean {
        return this.matchesPaths(item) || this.matchesType(item);
    }

    private matchesPaths(item: ContentSummary) {
        return this.filterContentPaths.some((path: ContentPath) => {
            if (item.getPath().equals(path) || item.getPath().isDescendantOf(path)) {
                return true;
            }
        });
    }

    private matchesType(item: ContentSummary) {
        return this.filterContentTypes.some((type: ContentTypeName) => {
            if (item.getType().equals(type)) {
                return true;
            }
        });
    }

    setFilterContentPaths(contentPaths: ContentPath[]) {
        this.filterContentPaths = contentPaths;
    }

    setFilterContentTypes(contentTypes: ContentTypeName[]) {
        this.filterContentTypes = contentTypes;
    }
}
