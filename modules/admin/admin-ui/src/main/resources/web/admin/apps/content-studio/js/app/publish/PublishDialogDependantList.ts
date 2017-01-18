import '../../api.ts';
import {DialogDependantList} from '../dialog/DependantItemsDialog';

import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import CompareStatus = api.content.CompareStatus;

export class PublishDialogDependantList extends DialogDependantList {

    private itemClickListeners: {(item: ContentSummaryAndCompareStatus): void}[] = [];

    private removeClickListeners: {(item: ContentSummaryAndCompareStatus): void}[] = [];

    clearItems() {
        this.removeClass('contains-removable');
        super.clearItems();
    }

    createItemView(item: ContentSummaryAndCompareStatus, readOnly: boolean): api.dom.Element {
        let view = super.createItemView(item, readOnly);

        if (CompareStatus.NEWER === item.getCompareStatus()) {
            view.addClass('removable');
            this.toggleClass('contains-removable', true);
        }

        view.onClicked((event) => {
            if (new api.dom.ElementHelper(<HTMLElement>event.target).hasClass('remove')) {
                this.notifyItemRemoveClicked(item);
            } else {
                this.notifyItemClicked(item);
            }
        });

        if (!isContentSummaryValid(item)) {
            view.addClass('invalid');
            view.getEl().setTitle('Edit invalid content');
        }

        return view;
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
