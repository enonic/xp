package com.enonic.cms.web.rest.account;

import com.enonic.cms.web.rest.common.LoadStoreRequest;

public final class UserLoadRequest
    extends LoadStoreRequest
{
    public String buildHqlQuery()
    {
        final StringBuilder str = new StringBuilder();
        str.append("x.deleted = 0");

        final String query = getQuery();
        if (query != null) {
            str.append(" AND x.displayName LIKE '%");
            str.append(query.replace("'", "''").replace("%", "%%"));
            str.append("%'");
        }

        return str.toString();
    }

    public String buildHqlOrder()
    {
        String property = getSort();

        if ("name".equalsIgnoreCase(property)) {
            property = "x.name";
        } else if ("userStore".equalsIgnoreCase(property)) {
            property = "x.userStore";
        } else if ("lastModified".equalsIgnoreCase(property)) {
            property = "x.timestamp";
        } else {
            property = "x.displayName";
        }

        final StringBuilder str = new StringBuilder();
        str.append(property).append(" ").append( getDir());
        return str.toString();
    }
}
