package com.oath.cyclops.jackson.fix;

import cyclops.container.Seq;
import javax.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Query {


    @XmlElement(name = "name")
    public final String name;


    @XmlElement(name = "queries")
    public final Seq<Query> queries;

    public Query() {
        this.queries = null;
        this.name = null;
    }


}
