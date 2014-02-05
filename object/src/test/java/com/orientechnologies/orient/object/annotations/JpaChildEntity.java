package com.orientechnologies.orient.object.annotations;

import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.metadata.schema.OClass;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author Sander Postma (s.postma@magsoft.nl)
 */
@Entity
public class JpaChildEntity
{
    @Id
    private ORID id;

    private String stringField1;

    @OAttributes(notNull = true, min = "10", max = "50")
    @OIndex(indexType = OClass.INDEX_TYPE.UNIQUE_HASH_INDEX)
    private String stringField2;

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
}
