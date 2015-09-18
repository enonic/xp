exports.addPageContribution = function (response, tagPos, contribution) {
    var pageContributions = response.pageContributions || {};
    response.pageContributions = pageContributions;
    var contributions = pageContributions[tagPos] || [];
    contributions = [].concat(contributions);
    pageContributions[tagPos] = contributions;
    contributions.push(contribution);
};
