module api.ui.tags {

    export class TagBuilder {

        value: string;

        removable: boolean;

        setValue(value: string): TagBuilder {
            this.value = value;
            return this;
        }

        setRemovable(value: boolean): TagBuilder {
            this.removable = value;
            return this;
        }

        public build(): Tag {
            return new Tag(this);
        }
    }

    export class Tag extends api.dom.LiEl {

        private removeButtonEl: api.dom.AEl;

        private valueHolderEl: api.dom.SpanEl;

        private value: string;

        private removable: boolean;

        private tagRemoveListeners: {(event: TagRemoveEvent) : void}[] = [];

        constructor(builder: TagBuilder) {
            super("tag");
            this.value = builder.value;

            this.valueHolderEl = new api.dom.SpanEl();
            this.valueHolderEl.setHtml(this.value);
            this.appendChild(this.valueHolderEl);

            this.removable = builder.removable;
            if (this.removable) {
                this.removeButtonEl = new api.dom.AEl("remove-button");
                this.appendChild(this.removeButtonEl);
                this.removeButtonEl.onClicked(() => {
                    this.notifyTagRemoved(new TagRemoveEvent(this.value));
                })
            }

            // TODO: Display value and remove icon if removable
            //  listen to clicks on remove icon and call notifyTagRemoved
        }

        getValue(): string {
            return this.value;
        }

        onTagRemove(listener: (event: TagRemoveEvent) => void) {
            this.tagRemoveListeners.push(listener);
        }

        unTagRemoved(listener: (event: TagRemoveEvent) => void) {
            this.tagRemoveListeners.push(listener);
        }

        private notifyTagRemoved(event: TagRemoveEvent) {
            this.tagRemoveListeners.forEach((listener: (event: TagRemoveEvent)=>void) => {
                listener(event);
            });
        }
    }
}