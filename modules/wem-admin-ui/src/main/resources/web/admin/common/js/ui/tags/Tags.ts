module api.ui.tags {

    export class TagsBuilder {

        tagSuggester: TagSuggester;

        tags: string[] = [];

        maxTags: number = 0;

        setTagSuggester(value: TagSuggester): TagsBuilder {
            this.tagSuggester = value;
            return this;
        }

        addTag(value: string): TagsBuilder {
            this.tags.push(value);
            return this;
        }

        setMaxTags(value: number): TagsBuilder {
            this.maxTags = value;
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

        private lastTag: Tag = null;

        private maxTags: number;

        private tagAddedListeners: {(event: TagAddedEvent) : void}[] = [];

        private tagRemovedListeners: {(event: TagRemovedEvent) : void}[] = [];

        constructor(builder: TagsBuilder) {
            super("tags");
            this.tagSuggester = builder.tagSuggester;

            builder.tags.forEach((value: string) => {
                this.doAddTag(value);
            });
            this.maxTags = builder.maxTags;

            this.textInput = new api.ui.text.TextInput();
            this.appendChild(this.textInput);

            this.textInput.onKeyUp((event: KeyboardEvent) => {
                if (event.keyCode == 32 || event.keyCode == 13) {
                    this.handleWordCompleted();
                }
            });
            // TODO: Listen to typing in text input and
            // make call to tagSuggester (if existing) and display result
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

        private handleWordCompleted() {
            var inputValue = this.textInput.getValue();
            var word = inputValue.trim();

            var tag = this.doAddTag(word);
            if (tag) {
                this.notifyTagAdded(new TagAddedEvent(tag.getValue()));
                this.textInput.setValue("");

                if (this.isMaxTagsReached()) {
                    this.textInput.hide();
                }
            }
        }

        clearTags() {

            this.tags.forEach((tag) => {
                tag.remove();
            });
            this.tags = [];
        }

        addTag(value: string) {

            var tag = this.doAddTag(value);
            if (tag) {
                if (this.isMaxTagsReached()) {
                    this.textInput.hide();
                }
            }
        }

        private doAddTag(value: string): Tag {
            if (this.hasTag(value)) {
                return null;
            }

            var tag = new TagBuilder().setValue(value).setRemovable(true).build();
            this.tags.push(tag);
            if (this.lastTag) {
                tag.insertAfterEl(this.lastTag);
            }
            else {
                this.prependChild(tag);
            }
            this.lastTag = tag;

            tag.onTagRemove((event: TagRemoveEvent) => {
                var index = this.getIndexOf(event.getValue());
                if (index >= 0) {
                    var tagToRemove = this.tags[index];
                    tagToRemove.remove();
                    this.tags.splice(index, 1);
                    if (!this.textInput.isVisible() && !this.isMaxTagsReached()) {
                        this.textInput.setVisible(true);
                    }
                    if (this.tags.length == 0) {
                        this.lastTag = null;
                    }
                    else {
                        this.lastTag = this.tags[this.tags.length - 1];
                    }
                    this.textInput.giveFocus();
                    this.notifyTagRemoved(new TagRemovedEvent(event.getValue(), index));
                }
            });

            return tag;
        }

        hasTag(value: string) {
            var match = false;
            this.tags.forEach((tag) => {
                if (value == tag.getValue()) {
                    match = true;
                }
            });
            return match;
        }

        countTags(): number {
            return this.tags.length;
        }

        getTags(): string[] {
            var tags: string [] = [];
            this.tags.forEach((tag) => {
                tags.push(tag.getValue());
            });
            return tags;
        }

        isMaxTagsReached(): boolean {
            if (this.maxTags == 0) {
                return false;
            }
            return this.countTags() >= this.maxTags;
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

        giveFocus(): boolean {
            if (this.isMaxTagsReached()) {
                return this.tags[0].giveFocus();
            }
            else {
                return this.textInput.giveFocus();
            }
        }

        private notifyTagRemoved(event: TagRemovedEvent) {
            this.tagRemovedListeners.forEach((listener: (event: TagRemovedEvent)=>void) => {
                listener(event);
            });
        }

    }
}