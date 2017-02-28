import '../../api.ts';
import {ContentSummaryOptionDataHelper} from './ContentSummaryOptionDataHelper';
import {ContentSummaryOptionDataLoader} from './ContentSummaryOptionDataLoader';

import SelectedOption = api.ui.selector.combobox.SelectedOption;
import MoveContentSummaryLoader = api.content.resource.MoveContentSummaryLoader;
import ContentSummary = api.content.ContentSummary;
import ContentSelectedOptionsView = api.content.ContentSelectedOptionsView;
import ContentPath = api.content.ContentPath;
import SelectedOptionsView = api.ui.selector.combobox.SelectedOptionsView;
import RichComboBoxBuilder = api.ui.selector.combobox.RichComboBoxBuilder;
import ContentTypeName = api.schema.content.ContentTypeName;

export class ContentMoveComboBox extends api.ui.selector.combobox.RichComboBox<ContentSummary> {

    private readonlyChecker: ReadonlyChecker;

    constructor() {
        let richComboBoxBuilder: RichComboBoxBuilder<ContentSummary> = new RichComboBoxBuilder<ContentSummary>();
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
        this.readonlyChecker = new ReadonlyChecker();
        this.getComboBox().getComboBoxDropdownGrid().setReadonlyChecker(this.readonlyChecker.isReadOnly.bind(this.readonlyChecker));
        this.onOptionDeselected(() => {
            this.getComboBox().getInput().reset();
        })
    }

    getLoader(): MoveContentSummaryLoader {
        return <MoveContentSummaryLoader> this.loader;
    }

    setFilterContents(contents: ContentSummary[]) {
        this.getLoader().setFilterContentPaths(contents.map((content) => content.getPath()));
        this.readonlyChecker.setFilterContentIds(contents.map((content) => content.getContentId()));
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

class ReadonlyChecker {

    private filterContentIds: ContentId[] = [];

    private filterContentTypes: ContentTypeName[] = [ContentTypeName.IMAGE, ContentTypeName.MEDIA, ContentTypeName.PAGE_TEMPLATE,
        ContentTypeName.FRAGMENT, ContentTypeName.MEDIA_DATA, ContentTypeName.MEDIA_AUDIO, ContentTypeName.MEDIA_ARCHIVE,
        ContentTypeName.MEDIA_VIDEO, ContentTypeName.MEDIA_CODE, ContentTypeName.MEDIA_EXECUTABLE, ContentTypeName.MEDIA_PRESENTATION,
        ContentTypeName.MEDIA_SPREADSHEET, ContentTypeName.MEDIA_UNKNOWN, ContentTypeName.MEDIA_DOCUMENT, ContentTypeName.MEDIA_VECTOR,
        ContentTypeName.SHORTCUT];

    isReadOnly(item: ContentSummary): boolean {
        return this.matchesIds(item) || this.matchesType(item);
    }

    private matchesIds(item: ContentSummary) {
        return this.filterContentIds.some((id: ContentId) => {
            if (item.getContentId().equals(id)) {
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

    setFilterContentIds(contentIds: ContentId[]) {
        this.filterContentIds = contentIds;
    }

    setFilterContentTypes(contentTypes: ContentTypeName[]) {
        this.filterContentTypes = contentTypes;
    }
}
