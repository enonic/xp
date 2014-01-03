module api.ui.grid {

    export class CollapsingItem<T>  {

        private actualItem:T;

        constructor(actualItem:T) {
            this.actualItem = actualItem;
        }
    }
}