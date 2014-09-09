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

        private tagSuggestions: TagSuggestions;

        private tags: Tag[] = [];

        private maxTags: number;

        private preservedValue: string;

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

            this.tagSuggestions = new TagSuggestions();
            this.tagSuggestions.hide();
            this.appendChild(this.tagSuggestions);

            this.textInput.onKeyDown((event: KeyboardEvent) => {
                if (event.keyCode == 188 || event.keyCode == 13) { // comma or enter
                    this.handleWordCompleted();
                    event.preventDefault();
                } else if (event.keyCode == 8) {
                    if (!this.textInput.getValue() && this.countTags() > 0) {
                        this.removeTag(this.tags[this.countTags() - 1]);
                    }
                } else if (event.keyCode == 38) {
                    if (this.tagSuggestions.isVisible()) {
                        var value = this.tagSuggestions.moveUp();
                        // call ElementHelper.setValue to avoid firing ValueChangedEvent
                        this.textInput.getEl().setValue(value || this.preservedValue);
                        event.preventDefault();
                    }
                } else if (event.keyCode == 40) {
                    if (this.tagSuggestions.isVisible()) {
                        var value = this.tagSuggestions.moveDown();
                        // call ElementHelper.setValue to avoid firing ValueChangedEvent
                        this.textInput.getEl().setValue(value || this.preservedValue);
                        event.preventDefault();
                    }
                }
            });
            this.textInput.onBlur((event: FocusEvent) => {
                this.handleWordCompleted();
            });

            this.textInput.onValueChanged((event: api.ui.ValueChangedEvent) => {

                var searchString = event.getNewValue();

                this.tagSuggester.suggest(searchString).then((values: string[]) => {

                    var existingValues = this.getTags().concat(searchString);
                    values = values.filter((value: string) => (existingValues.indexOf(value) < 0));

                    if (values.length == 0) {
                        this.tagSuggestions.hide();
                    } else {
                        this.tagSuggestions.setTags(values);
                        this.tagSuggestions.getEl().
                            setTopPx(this.textInput.getEl().getOffsetToParent().top + this.textInput.getEl().getHeightWithMargin()).
                            setLeftPx(this.textInput.getEl().getOffsetToParent().left);
                        this.tagSuggestions.show();
                        this.preservedValue = searchString;
                    }
                }).done();
            });

            this.onClicked(() => this.textInput.giveFocus());
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
            this.tags.forEach((tag) => tag.remove());
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
            if (this.hasTag(value) || !value) {
                return null;
            }

            var tag = new TagBuilder().setValue(value).setRemovable(true).build();
            this.tags.push(tag);
            tag.insertBeforeEl(this.textInput);

            tag.onTagRemove(() => this.removeTag(tag));

            return tag;
        }

        private removeTag(tag: Tag) {
            var index = this.tags.indexOf(tag);
            if (index >= 0) {
                tag.remove();
                this.tags.splice(index, 1);
                if (!this.textInput.isVisible() && !this.isMaxTagsReached()) {
                    this.textInput.setVisible(true);
                }
                this.textInput.giveFocus();
                this.notifyTagRemoved(new TagRemovedEvent(tag.getValue(), index));
            }
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
            return this.tags.map((tag: Tag) => tag.getValue());
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