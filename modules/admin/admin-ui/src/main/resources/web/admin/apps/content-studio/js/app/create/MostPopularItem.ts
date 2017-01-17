import '../../api.ts';
import {NewContentDialogListItem} from './NewContentDialogListItem';

import ContentTypeSummary = api.schema.content.ContentTypeSummary;

export class MostPopularItem extends NewContentDialogListItem {

    private hits: number;

    constructor(contentType: ContentTypeSummary, hits: number) {
        super(contentType);

        this.hits = hits;
    }

    getHits(): number {
        return this.hits;
    }
}
