module api.liveedit {

    export class RepeatNextItemViewIdProducer extends ItemViewIdProducer {

        private idToRepeatNext: ItemViewId;

        private itemViewProducer: ItemViewIdProducer;

        private repeated: boolean;

        constructor(idToRepeatNext: ItemViewId, itemViewProducer: ItemViewIdProducer) {
            super();
            this.idToRepeatNext = idToRepeatNext;
            this.itemViewProducer = itemViewProducer;
            this.repeated = false;
        }

        next(): ItemViewId {
            if (!this.repeated) {
                this.repeated = true;
                return this.idToRepeatNext;
            }
            else {
                return this.itemViewProducer.next();
            }
        }
    }
}