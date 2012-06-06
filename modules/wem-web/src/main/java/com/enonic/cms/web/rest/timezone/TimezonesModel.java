package com.enonic.cms.web.rest.timezone;

import java.util.ArrayList;
import java.util.List;

public class TimezonesModel
{
    private int total;
    private List<TimezoneModel> timezones;

    public TimezonesModel()
    {
        this.timezones = new ArrayList<TimezoneModel>();
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<TimezoneModel> getTimezones()
    {
        return timezones;
    }

    public void setTimezones( List<TimezoneModel> timezones )
    {
        this.timezones = timezones;
    }

    public void addTimezone(TimezoneModel timezone)
    {
        this.timezones.add(timezone);
    }
}
