module api.content.json {

    export interface ContentsExistJson {

        contentsExistJson: ContentExistJson[];
    }

    export interface ContentExistJson {

        contentId: string;

        exists: boolean;
    }
}