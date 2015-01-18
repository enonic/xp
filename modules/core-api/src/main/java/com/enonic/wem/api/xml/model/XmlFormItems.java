package com.enonic.wem.api.xml.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

import com.google.common.collect.Lists;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "formItems")
public class XmlFormItems
{
    @XmlElements({@XmlElement(name = "input", type = XmlInput.class), @XmlElement(name = "mixin-reference", type = XmlMixinReference.class),
                     @XmlElement(name = "field-set", type = XmlFieldSet.class),
                     @XmlElement(name = "form-item-set", type = XmlFormItemSet.class)})
    private List<XmlFormItem> items = Lists.newArrayList();

    public List<XmlFormItem> getItems()
    {
        return this.items;
    }
}
