package com.orientechnologies.orient.object.annotations;

import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.object.annotations.JpaChildEntity;
import com.orientechnologies.orient.object.annotations.OAttributes;
import com.orientechnologies.orient.object.annotations.OIndex;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sander Postma (s.postma@magsoft.nl)
 */
@Entity
public class JpaTestEntity
{
    @Id
    private ORID id;

    @Column(nullable = false, updatable = false, length = 20)
    @OAttributes(mandatory = true, notNull = true, min = "10", max = "60")
    private String stringField1;

    @Column(nullable = false, updatable = false, length = 20)
    private String stringField2;

    @OIndex(indexType = OClass.INDEX_TYPE.UNIQUE_HASH_INDEX)
    private String stringField3;

    private String stringField4;

    @Column(updatable = false)
    private Integer intField1;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "TESTID")
    @OAttributes(mandatory = true)
    private List<JpaChildEntity> children = new ArrayList<JpaChildEntity>();


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

    public Integer getIntField1()
    {
        return intField1;
    }

    public void setIntField1(Integer intField1)
    {
        this.intField1 = intField1;
    }

    public List<JpaChildEntity> getChildren()
    {
        return children;
    }

    public void setChildren(List<JpaChildEntity> children)
    {
        this.children = children;
    }

    public ORID getId()
    {
        return id;
    }

    public String getStringField4()
    {
        return stringField4;
    }

    public void setStringField4(String stringField4)
    {
        this.stringField4 = stringField4;
    }
}
