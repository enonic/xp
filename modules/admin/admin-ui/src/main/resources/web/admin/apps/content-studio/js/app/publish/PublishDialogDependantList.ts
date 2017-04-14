import '../../api.ts';
import {DialogDependantList} from '../dialog/DependantItemsDialog';
import {StatusSelectionItem} from '../dialog/StatusSelectionItem';

import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import CompareStatus = api.content.CompareStatus;
import ContentIds = api.content.ContentIds;

export class PublishDialogDependantList extends DialogDependantList {

    private requiredIds: ContentIds;

    private itemClickListeners: {(item: ContentSummaryAndCompareStatus): void}[] = [];

    private removeClickListeners: {(item: ContentSummaryAndCompareStatus): void}[] = [];

    constructor() {
        super();

        this.addClass('publish-dialog-dependant-list');
        this.requiredIds = ContentIds.empty();
    }

    createItemView(item: ContentSummaryAndCompareStatus, readOnly: boolean): api.dom.Element {
        let view = super.createItemView(item, readOnly);

        if (!this.requiredIds.contains(item.getContentId())) {
            view.addClass('removable');
        }

        (<StatusSelectionItem>view).setIsRemovableFn(() => !this.requiredIds.contains(item.getContentId()));
        (<StatusSelectionItem>view).setRemoveHandlerFn(() => this.notifyItemRemoveClicked(item));

        view.onClicked((event) => {
            if (!new api.dom.ElementHelper(<HTMLElement>event.target).hasClass('remove')) {
                this.notifyItemClicked(item);
            }
        });

        if (!isContentSummaryValid(item)) {
            view.addClass('invalid');
            view.getEl().setTitle('Edit invalid content');
        }

        return view;
    }

    setRequiredIds(value: ContentId[]) {
        this.requiredIds = ContentIds.from(value);
    }

    onItemClicked(listener: (item: ContentSummaryAndCompareStatus) => void) {
        this.itemClickListeners.push(listener);
    }

    unItemClicked(listener: (item: ContentSummaryAndCompareStatus) => void) {
        this.itemClickListeners = this.itemClickListeners.filter((curr) => {
            return curr !== listener;
        });
    }

    private notifyItemClicked(item: ContentSummaryAndCompareStatus) {
        this.itemClickListeners.forEach(listener => {
            listener(item);
        });
    }

    onItemRemoveClicked(listener: (item: ContentSummaryAndCompareStatus) => void) {
        this.removeClickListeners.push(listener);
    }

    unItemRemoveClicked(listener: (item: ContentSummaryAndCompareStatus) => void) {
        this.removeClickListeners = this.removeClickListeners.filter((curr) => {
            return curr !== listener;
        });
    }

    private notifyItemRemoveClicked(item: ContentSummaryAndCompareStatus) {
        this.removeClickListeners.forEach(listener => {
            listener(item);
        });
    }
}

export function isContentSummaryValid(item: ContentSummaryAndCompareStatus): boolean {
    let status = item.getCompareStatus();
    let summary = item.getContentSummary();

    return status === CompareStatus.PENDING_DELETE ||
           (summary.isValid() && !api.util.StringHelper.isBlank(summary.getDisplayName()) && !summary.getName().isUnnamed());
}
