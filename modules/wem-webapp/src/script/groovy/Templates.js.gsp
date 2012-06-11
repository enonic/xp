Templates.${templateNamespace} = {
<% for (tpl in templateList) { %>
    <%= tpl.name %>:
${tpl.text}<% if ( ! tpl.is(templateList.last()) ) { %>,<% } %>
<% } %>
};