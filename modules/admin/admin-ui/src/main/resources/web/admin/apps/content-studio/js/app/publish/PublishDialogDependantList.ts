import '../../api.ts';
import {DialogDependantList} from '../dialog/DependantItemsDialog';

import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import CompareStatus = api.content.CompareStatus;
import ContentIds = api.content.ContentIds;
import Tooltip = api.ui.Tooltip;

export class PublishDialogDependantList extends DialogDependantList {

    private requiredIds: ContentIds;

    private itemClickListeners: {(item: ContentSummaryAndCompareStatus): void}[] = [];

    private removeClickListeners: {(item: ContentSummaryAndCompareStatus): void}[] = [];

    constructor() {
        super();

        this.requiredIds = ContentIds.empty();
    }

    createItemView(item: ContentSummaryAndCompareStatus, readOnly: boolean): api.dom.Element {
        let view = super.createItemView(item, readOnly);
        let isRemovable = !this.requiredIds.contains(item.getContentId());

        if (isRemovable) {
            view.addClass('removable');
        }

        let onViewClicked = api.util.AppHelper.debounce((event) => {
            if (new api.dom.ElementHelper(<HTMLElement>event.target).hasClass('remove')) {
                if (isRemovable) {
                    this.notifyItemRemoveClicked(item);
                } else {
                    let tooltip = new Tooltip(api.dom.Element.fromHtmlElement(<HTMLElement>event.target), 'This item is required for publishing');
                    tooltip.setTrigger(Tooltip.TRIGGER_NONE);
                    tooltip.showFor(1500);
                }
            } else {
                this.notifyItemClicked(item);
            }
        }, 1000, true);


        view.onClicked(onViewClicked);

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
