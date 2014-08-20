module api.ui.tags {

    export class TagSuggestions extends api.dom.UlEl {

        private selectedIndex: number = null;

        constructor() {
            super('suggestions');
        }

        setTags(values: string[]) {
            this.removeChildren();
            values.forEach((value: string) => {
                this.appendChild(new api.dom.LiEl().setHtml(value));
            });
            this.selectedIndex = null;
        }

        moveDown():string {
            var nextIndex:number;
            if (this.selectedIndex == null) {
                nextIndex = 0;
            } else if (this.selectedIndex == this.getChildren().length - 1) {
                nextIndex = null;
            } else {
                nextIndex = this.selectedIndex + 1;
            }

            return this.select(nextIndex);
        }

        moveUp():string {
            var nextIndex:number;
            if (this.selectedIndex == null) {
                nextIndex = this.getChildren().length - 1;
            } else if (this.selectedIndex == 0) {
                nextIndex = null;
            } else {
                nextIndex = this.selectedIndex - 1;
            }

            return this.select(nextIndex);
        }

        private select(index: number):string {
            var tags = this.getChildren();
            var tag = tags[this.selectedIndex];
            if (tag) {
                tag.removeClass('selected');
            }

            this.selectedIndex = index;
            tag = tags[this.selectedIndex];
            if (tag) {
                tag.addClass('selected');
                return tag.getEl().getText();
            } else {
                return null;
            }
        }

    }

}