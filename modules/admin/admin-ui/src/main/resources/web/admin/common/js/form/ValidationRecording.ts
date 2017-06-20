module api.form {

    export class ValidationRecording {

        breaksMinimumOccurrencesArray: ValidationRecordingPath[] = [];

        breaksMaximumOccurrencesArray: ValidationRecordingPath[] = [];

        breaksMinimumOccurrences(path: ValidationRecordingPath) {
            if (!this.exists(path, this.breaksMinimumOccurrencesArray)) {
                this.breaksMinimumOccurrencesArray.push(path);
            }
        }

        breaksMaximumOccurrences(path: ValidationRecordingPath) {
            if (!this.exists(path, this.breaksMaximumOccurrencesArray)) {
                this.breaksMaximumOccurrencesArray.push(path);
            }
        }

        isValid(): boolean {
            return this.breaksMinimumOccurrencesArray.length === 0 && this.breaksMaximumOccurrencesArray.length === 0;
        }

        isMinimumOccurrencesValid(): boolean {
            return this.breaksMinimumOccurrencesArray.length === 0;
        }

        isMaximumOccurrencesValid(): boolean {
            return this.breaksMaximumOccurrencesArray.length === 0;
        }

        flatten(recording: ValidationRecording) {

            recording.breaksMinimumOccurrencesArray.forEach((path: ValidationRecordingPath)=> {
                this.breaksMinimumOccurrences(path);
            });

            recording.breaksMaximumOccurrencesArray.forEach((path: ValidationRecordingPath)=> {
                this.breaksMaximumOccurrences(path);
            });
        }

        subtract(recording: ValidationRecording) {
            this.breaksMinimumOccurrencesArray = this.breaksMinimumOccurrencesArray.filter((path: ValidationRecordingPath)=> {
                return !this.exists(path, recording.breaksMinimumOccurrencesArray);
            });

            this.breaksMaximumOccurrencesArray = this.breaksMaximumOccurrencesArray.filter((path: ValidationRecordingPath)=> {
                return !this.exists(path, recording.breaksMaximumOccurrencesArray);
            });
        }

        /**
         * @param path - path to remove
         * @param strict - whether to match only exact matching paths
         * @param includeChildren - param saying if nested children should be removed as well
         */
        removeByPath(path: ValidationRecordingPath, strict?: boolean, includeChildren?: boolean) {

            this.removeUnreachedMinimumOccurrencesByPath(path, strict, includeChildren);
            this.removeBreachedMaximumOccurrencesByPath(path, strict, includeChildren);
        }

        removeUnreachedMinimumOccurrencesByPath(path: ValidationRecordingPath, strict?: boolean, includeChildren?: boolean) {

            for (let i = this.breaksMinimumOccurrencesArray.length - 1; i >= 0; i--) {
                let currentPath = this.breaksMinimumOccurrencesArray[i];
                let remove = currentPath.equals(path) ||
                             includeChildren && (strict && currentPath.contains(path) ||
                                                 !strict && currentPath.toString().indexOf(path.toString()) === 0);
                if (remove) {
                    this.breaksMinimumOccurrencesArray.splice(i, 1);
                    if (!includeChildren) {
                        break;
                    }
                }
            }
        }

        removeBreachedMaximumOccurrencesByPath(path: ValidationRecordingPath, strict?: boolean, includeChildren?: boolean) {

            for (let i = this.breaksMaximumOccurrencesArray.length - 1; i >= 0; i--) {
                let currentPath = this.breaksMaximumOccurrencesArray[0];
                let remove = currentPath.equals(path) ||
                             includeChildren && (strict && currentPath.contains(path) ||
                                                 !strict && currentPath.toString().indexOf(path.toString()) === 0);
                if (remove) {
                    this.breaksMaximumOccurrencesArray.splice(i, 1);
                    if (!includeChildren) {
                        break;
                    }
                }
            }
        }

        equals(other: ValidationRecording): boolean {

            if (this.breaksMinimumOccurrencesArray.length !== other.breaksMinimumOccurrencesArray.length) {
                return false;
            } else if (this.breaksMaximumOccurrencesArray.length !== other.breaksMaximumOccurrencesArray.length) {
                return false;
            }

            for (let i = 0; i < this.breaksMinimumOccurrencesArray.length; i++) {
                if (this.breaksMinimumOccurrencesArray[i].toString() !== other.breaksMinimumOccurrencesArray[i].toString()) {
                    return false;
                }
            }

            for (let i = 0; i < this.breaksMaximumOccurrencesArray.length; i++) {
                if (this.breaksMaximumOccurrencesArray[i].toString() !== other.breaksMaximumOccurrencesArray[i].toString()) {
                    return false;
                }
            }

            return true;
        }

        validityChanged(previous: api.form.ValidationRecording): boolean {
            return previous == null || !previous.equals(this);
        }

        containsPathInBreaksMin(path: ValidationRecordingPath) {
            return this.exists(path, this.breaksMinimumOccurrencesArray);
        }

        containsPathInBreaksMax(path: ValidationRecordingPath) {
            return this.exists(path, this.breaksMaximumOccurrencesArray);
        }

        private exists(path: ValidationRecordingPath, array: ValidationRecordingPath[]): boolean {
            for (let i = 0; i < array.length; i++) {
                if (array[i].toString() === path.toString()) {
                    return true;
                }
            }
            return false;
        }
    }
}
