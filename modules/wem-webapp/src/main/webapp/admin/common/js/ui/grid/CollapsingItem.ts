module api_ui_grid {

    export class CollapsingItem<T>  {

        private actualItem:T;

        constructor(actualItem:T) {
            this.actualItem = actualItem;
        }
    }
}