package com.patimer.notifier.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@Document(collection = "searcher_findings")
public class SearcherFindings
{
    @Id
    private UUID searcherId;

    @NotNull
    private Date lastSearchTime;

    private Object findings;

    protected SearcherFindings(){} // default constructor required by mongodb


}
