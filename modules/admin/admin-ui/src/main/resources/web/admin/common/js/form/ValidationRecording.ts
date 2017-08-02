module api.form {

    import ArrayHelper = api.util.ArrayHelper;

    export class ValidationRecording {

        private breaksMinimumOccurrencesArray: ValidationRecordingPath[] = [];

        private breaksMaximumOccurrencesArray: ValidationRecordingPath[] = [];

        private additionalValidationRecords: Map<string,AdditionalValidationRecord> = new Map<string,AdditionalValidationRecord>();

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

        addValidationRecord(path: string, record: AdditionalValidationRecord) {
            this.additionalValidationRecords.set(path, record);
        }

        isValid(): boolean {
            return this.breaksMinimumOccurrencesArray.length === 0 && this.breaksMaximumOccurrencesArray.length === 0
                   && this.additionalValidationRecords.size == 0;
        }

        isMinimumOccurrencesValid(): boolean {
            return this.breaksMinimumOccurrencesArray.length === 0;
        }

        isMaximumOccurrencesValid(): boolean {
            return this.breaksMaximumOccurrencesArray.length === 0;
        }

        getBreakMinimumOccurrences(): ValidationRecordingPath[] {
            return this.breaksMinimumOccurrencesArray;
        }

        getBreakMaximumOccurrences(): ValidationRecordingPath[] {
            return this.breaksMaximumOccurrencesArray;
        }

        flatten(recording: ValidationRecording) {

            recording.breaksMinimumOccurrencesArray.forEach((path: ValidationRecordingPath)=> {
                this.breaksMinimumOccurrences(path);
            });

            recording.breaksMaximumOccurrencesArray.forEach((path: ValidationRecordingPath)=> {
                this.breaksMaximumOccurrences(path);
            });

            recording.additionalValidationRecords.forEach((value: AdditionalValidationRecord, key: string)=> {
                this.addValidationRecord(key, value);
            });
        }

        removeRecord(key: string) {
            this.additionalValidationRecords.delete(key);
        }

        /**
         * @param path - path to remove
         * @param strict - whether to match only exact matching paths
         * @param includeChildren - param saying if nested children should be removed as well
         */
        removeByPath(path: ValidationRecordingPath, strict?: boolean, includeChildren?: boolean) {

            this.removeUnreachedMinimumOccurrencesByPath(path, strict, includeChildren);
            this.removeBreachedMaximumOccurrencesByPath(path, strict, includeChildren);
            this.removeRecord(path.toString());
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
            } else if (this.additionalValidationRecords.size !== other.additionalValidationRecords.size) {
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

            if (!this.mapEquals(this.additionalValidationRecords, other.additionalValidationRecords)) {
                return false;
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

        /*
         * Should be moved to ObjectHelper.ts after changing gulp to webpack in common module
         * */
        private mapEquals(mapA: Map<string, Equitable>, mapB: Map<string, Equitable>): boolean {
            if (mapA.size != mapB.size) {
                return false;
            }

            const keys = mapA.keys();
            let result: IteratorResult<string>;

            do {
                result = keys.next();

                if (!result.done) {
                    const key: string = result.value;

                    if (!mapA.get(key).equals(mapB.get(key))) {
                        return false;
                    }
                }

            } while (!result.done);

            return true;
        }
    }
}
