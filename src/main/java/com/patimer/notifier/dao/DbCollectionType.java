package com.patimer.notifier.dao;

public enum DbCollectionType
{
    Accounts("accounts"),
    Searchers("searchers"),
    SearcherStoredData("searchers_stored_data");

    private String dbTableName;

    DbCollectionType(String dbTableName)
    {
        this.dbTableName = dbTableName;
    }

    public String getDbTableName()
    {
        return dbTableName;
    }
}
