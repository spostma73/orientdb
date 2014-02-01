package com.orientechnologies.orient.object.annotations;

import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.metadata.schema.OClass;

import javax.persistence.Column;
import javax.persistence.Id;

/**
 * @author Sander Postma (s.postma@magsoft.nl)
 */
public class TestEntity
{
    @Id
    private ORID id;

    @Column(nullable = false, updatable = false, length = 20)
    @OAttributes(mandatory = true, notNull = true, min = "10", max = "50")
    private String stringField1;

    @Column(nullable = false, updatable = false, length = 20)
    private String stringField2;

    @OAttributes(notNull = true)
    @OIndex(indexType = OClass.INDEX_TYPE.UNIQUE_HASH_INDEX)
    private String stringField3;

    @OIndex(name = "ixS4", indexType = OClass.INDEX_TYPE.UNIQUE_HASH_INDEX)
    private String stringField4a;

    @OIndex(name = "ixS4", indexType = OClass.INDEX_TYPE.UNIQUE_HASH_INDEX)
    private String stringField4b;

    @Column(updatable = false)
    private Integer intField1;

    @OAttributes(min = "5", max = "1000000", notNull = true, mandatory = true)
    private Integer intField2;

    public ORID getId()
    {
        return id;
    }

    public String getStringField1()
    {
        return stringField1;
    }

    public void setStringField1(String stringField1)
    {
        this.stringField1 = stringField1;
    }

    public String getStringField2()
    {
        return stringField2;
    }

    public void setStringField2(String stringField2)
    {
        this.stringField2 = stringField2;
    }

    public String getStringField3()
    {
        return stringField3;
    }

    public void setStringField3(String stringField3)
    {
        this.stringField3 = stringField3;
    }

    public String getStringField4a()
    {
        return stringField4a;
    }

    public void setStringField4a(String stringField4a)
    {
        this.stringField4a = stringField4a;
    }

    public String getStringField4b()
    {
        return stringField4b;
    }

    public void setStringField4b(String stringField4b)
    {
        this.stringField4b = stringField4b;
    }

    public Integer getIntField1()
    {
        return intField1;
    }

    public void setIntField1(Integer intField1)
    {
        this.intField1 = intField1;
    }

    public Integer getIntField2()
    {
        return intField2;
    }

    public void setIntField2(Integer intField2)
    {
        this.intField2 = intField2;
    }
}
