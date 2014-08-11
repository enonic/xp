module api.ui.tags {

    export class TagsBuilder {

        tagSuggester: TagSuggester;

        tags: string[] = [];

        setTagSuggester(value: TagSuggester): TagsBuilder {
            this.tagSuggester = value;
            return this;
        }

        addTag(value: string): TagsBuilder {
            this.tags.push(value);
            return this;
        }

        public build(): Tags {
            return new Tags(this);
        }
    }

    export class Tags extends api.dom.UlEl {

        private tagSuggester: TagSuggester;

        private textInput: api.ui.text.TextInput;

        private tags: Tag[] = [];

        private tagAddedListeners: {(event: TagAddedEvent) : void}[] = [];

        private tagRemovedListeners: {(event: TagRemovedEvent) : void}[] = [];

        constructor(builder: TagsBuilder) {
            super("tags");
            this.tagSuggester = builder.tagSuggester;

            builder.tags.forEach((value: string) => {
                var tag = this.createTag(value);
                this.tags.push(tag);
                this.appendChild(tag);
            });

            this.textInput = new api.ui.text.TextInput();
            this.appendChild(this.textInput);

            // TODO: Listen to typing in text input and
            // make call to tagSuggester (if existing) and display result

            // TODO: When user finish a word (space or enter)
            //  create and add Tag to this.tags and call notifyTagAdded (NB: hinder adding duplicates )
            //  also listen to TagRemovedEvent and remove tag from this.tags and DOM and call notifyTagRemoved

        }

        private createTag(value: string): Tag {
            var tag = new TagBuilder().setValue(value).setRemovable(true).build();
            tag.onTagRemove((event: TagRemoveEvent) => {
                var index = this.getIndexOf(event.getValue());
                if (index >= 0) {
                    var tagToRemove = this.tags[index];
                    tagToRemove.remove();
                    this.notifyTagRemoved(new TagRemovedEvent(event.getValue(), index));
                }
            });
            return tag;
        }

        private getIndexOf(value: string) {
            var matchingIndex = -1;
            this.tags.forEach((tag, index: number) => {
                if (tag.getValue() == value) {
                    matchingIndex = index;
                }
            });
            return matchingIndex;
        }

        clearTags() {

            this.tags.forEach((tag) => {
                tag.remove();
            });
            this.tags = [];
        }

        addTag(value: string) {
            var tag = new TagBuilder().setValue(value).setRemovable(true).build();
            this.tags.push(tag);
            this.textInput.prependChild(tag);
        }

        onTagAdded(listener: (event: TagAddedEvent) => void) {
            this.tagAddedListeners.push(listener);
        }

        unTagAdded(listener: (event: TagAddedEvent) => void) {
            this.tagAddedListeners.push(listener);
        }

        private notifyTagAdded(event: TagAddedEvent) {
            this.tagAddedListeners.forEach((listener: (event: TagAddedEvent)=>void) => {
                listener(event);
            });
        }

        onTagRemoved(listener: (event: TagRemovedEvent) => void) {
            this.tagRemovedListeners.push(listener);
        }

        unTagRemoved(listener: (event: TagRemovedEvent) => void) {
            this.tagRemovedListeners.push(listener);
        }

        private notifyTagRemoved(event: TagRemovedEvent) {
            this.tagRemovedListeners.forEach((listener: (event: TagRemovedEvent)=>void) => {
                listener(event);
            });
        }

    }
}