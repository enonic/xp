module api.ui.tags {

    export class TagSuggestions extends api.dom.UlEl {

        private selectedIndex: number = null;

        private selectedListeners: {(value: string) : void}[] = [];

        constructor() {
            super('tag-suggestions');

            this.onMouseMove((event: MouseEvent) => {
                // don't wrap element in ElementHelper because mousemove event is generated very frequently
                // unnecessary new objects would clog browser memory
                var htmlEl = <HTMLElement>event.target;
                if (htmlEl.tagName == 'LI') {
                    this.notifySelected(htmlEl.innerText || htmlEl.textContent);
                }
            });
        }

        setTags(values: string[]) {
            this.removeChildren();
            values.forEach((value: string) => {
                this.appendChild(new api.dom.LiEl().setHtml(value));
            });
            this.selectedIndex = null;
        }

        moveDown() {
            var nextIndex:number;
            if (this.selectedIndex == null) {
                nextIndex = 0;
            } else if (this.selectedIndex == this.getChildren().length - 1) {
                nextIndex = null;
            } else {
                nextIndex = this.selectedIndex + 1;
            }

            this.select(nextIndex);
        }

        moveUp() {
            var nextIndex:number;
            if (this.selectedIndex == null) {
                nextIndex = this.getChildren().length - 1;
            } else if (this.selectedIndex == 0) {
                nextIndex = null;
            } else {
                nextIndex = this.selectedIndex - 1;
            }

            this.select(nextIndex);
        }

        private select(index: number) {
            var tags = this.getChildren();
            var tag = tags[this.selectedIndex];
            if (tag) {
                tag.removeClass('selected');
            }

            this.selectedIndex = index;
            tag = tags[this.selectedIndex];
            if (tag) {
                tag.addClass('selected');
                this.notifySelected(tag.getEl().getText());
            } else {
                this.notifySelected(null);
            }
        }

        onSelected(listener: (value: string) => void) {
            this.selectedListeners.push(listener);
        }

        unSelected(listener: (value: string) => void) {
            this.selectedListeners.push(listener);
        }

        private notifySelected(value: string) {
            this.selectedListeners.forEach((listener: (value: string)=>void) => listener(value));
        }

    }

}