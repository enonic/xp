module api.liveedit {

    export class CreateItemViewConfig<PARENT extends ItemView, DATA> {

        itemViewProducer: ItemViewIdProducer;

        parentView: PARENT;

        parentElement: api.dom.Element;

        data: DATA;

        element: api.dom.Element;

        positionIndex: number = -1;

        /**
         * Optional. The ItemViewIdProducer of parentRegionView will be used if not set.
         */
        setItemViewProducer(value: ItemViewIdProducer): CreateItemViewConfig<PARENT,DATA> {
            this.itemViewProducer = value;
            return this;
        }

        setParentView(value: PARENT): CreateItemViewConfig<PARENT,DATA> {
            this.parentView = value;
            return this;
        }

        setParentElement(value: api.dom.Element): CreateItemViewConfig<PARENT,DATA> {
            this.parentElement = value;
            return this;
        }

        setData(value: DATA): CreateItemViewConfig<PARENT,DATA> {
            this.data = value;
            return this;
        }

        setElement(value: api.dom.Element): CreateItemViewConfig<PARENT,DATA> {
            this.element = value;
            return this;
        }

        /**
         * Optional. If not set then ItemView should be added as last child.
         */
        setPositionIndex(value: number): CreateItemViewConfig<PARENT,DATA> {
            this.positionIndex = value;
            return this;
        }
    }
}