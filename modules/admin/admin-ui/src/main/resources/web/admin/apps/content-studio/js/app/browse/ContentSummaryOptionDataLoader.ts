import '../../api.ts';


import TreeNode = api.ui.treegrid.TreeNode;
import ContentSummary = api.content.ContentSummary;
import ContentSummaryFetcher = api.content.resource.ContentSummaryFetcher;
import OptionDataLoader = api.ui.selector.OptionDataLoader;
import OptionDataLoaderData = api.ui.selector.OptionDataLoaderData;
import ContentResponse = api.content.resource.result.ContentResponse;
import Option = api.ui.selector.Option;

export class ContentSummaryOptionDataLoader implements OptionDataLoader<ContentSummary> {

    fetch(node: TreeNode<Option<ContentSummary>>): wemQ.Promise<ContentSummary> {
        return ContentSummaryFetcher.fetch(node.getData().displayValue.getContentId());
    }

    fetchChildren(parentNode: TreeNode<Option<ContentSummary>>, from: number = 0,
                  size: number = -1): wemQ.Promise<OptionDataLoaderData<ContentSummary>> {
        return ContentSummaryFetcher.fetchChildren(parentNode.getData() ? parentNode.getData().displayValue.getContentId() : null, from,
            size).then((response: ContentResponse<ContentSummary>) => {
            return new OptionDataLoaderData(response.getContents(), response.getMetadata().getHits(), response.getMetadata().getTotalHits());
        });
    }

    checkReadonly(contentSummaries: ContentSummary[]): wemQ.Promise<string[]> {
        return ContentSummaryFetcher.getReadOnly(contentSummaries);
    }
}
