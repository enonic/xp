module api.liveedit {

    export class CreateItemViewConfig<PARENT extends ItemView, DATA> {

        itemViewProducer: ItemViewIdProducer;

        parentView: PARENT;

        data: DATA;

        element: HTMLElement;

        /**
         * Optional. The ItemViewIdProducer of parentRegionView will be used if not set.
         */
        setItemViewProducer(value: ItemViewIdProducer): CreateItemViewConfig<PARENT,DATA> {
            this.itemViewProducer = value;
            return this;
        }

        setParent(value: PARENT): CreateItemViewConfig<PARENT,DATA> {
            this.parentView = value;
            return this;
        }

        setData(value: DATA): CreateItemViewConfig<PARENT,DATA> {
            this.data = value;
            return this;
        }

        setElement(value: HTMLElement): CreateItemViewConfig<PARENT,DATA> {
            this.element = value;
            return this;
        }
    }
}