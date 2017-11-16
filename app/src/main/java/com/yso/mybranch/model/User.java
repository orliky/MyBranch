package com.yso.mybranch.model;

/**
 * Created by Admin on 15-Nov-17.
 */

public class User
{
    private int id;
    private String name;
    private Branch branch;

    public User()
    {
    }

    public User(int id, String name, Branch branch)
    {
        super();
        this.id = id;
        this.name = name;
        this.branch = branch;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Branch getBranch()
    {
        return branch;
    }

    public void setBranch(Branch branch)
    {
        this.branch = branch;
    }

    @Override
    public String toString()
    {
        return "User [id=" + id + ", name=" + name + ", branch=" + ((branch == null) ? "N/A" : branch.getName()) + "]";
    }
}
