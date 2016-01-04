module api.security.auth {
    export class AuthService implements api.item.Item {

        private key: string;
        private displayName: string;

        constructor(json: AuthServiceJson) {
            this.key = json.key;
            this.displayName = json.displayName;
        }

        getKey(): string {
            return this.key;
        }

        getDisplayName(): string {
            return this.displayName;
        }


        getId(): string {
            return this.key;
        }

        getCreatedTime(): Date {
            return undefined;
        }

        getModifiedTime(): Date {
            return undefined;
        }

        isDeletable(): boolean {
            return false;
        }

        isEditable(): boolean {
            return false;
        }

    }
}