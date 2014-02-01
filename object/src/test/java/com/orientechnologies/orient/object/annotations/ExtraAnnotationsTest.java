package com.orientechnologies.orient.object.annotations;

import com.orientechnologies.orient.core.index.*;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Sander Postma (s.postma@magsoft.nl)
 */
public class ExtraAnnotationsTest
{
    private OObjectDatabaseTx databaseTx;

    @BeforeClass
    protected void setUp() throws Exception
    {
        databaseTx = new OObjectDatabaseTx("memory:ExtraAnnotationsTest");
        databaseTx.create();
        databaseTx.setAutomaticSchemaGeneration(true);
        databaseTx.getEntityManager().registerEntityClass(TestEntity.class);
    }

    @AfterClass
    protected void tearDown()
    {
        databaseTx.close();
    }


    @Test
    public void verifySchema()
    {
        OClass cls = databaseTx.getMetadata().getSchema().getClass(TestEntity.class);
        for (OProperty prop : cls.properties())
        {
            if (prop.getName().equals("stringField1"))
            {
                assertTrue(Boolean.TRUE.equals(prop.get(OProperty.ATTRIBUTES.MANDATORY)));
                assertTrue(Boolean.TRUE.equals(prop.get(OProperty.ATTRIBUTES.NOTNULL)));
                assertTrue("10".equals(prop.get(OProperty.ATTRIBUTES.MIN)));
                assertTrue("50".equals(prop.get(OProperty.ATTRIBUTES.MAX)));
            }
            else if (prop.getName().equals("stringField4b"))
            {
                for(OIndex<?> index : prop.getAllIndexes())
                {
                    assertEquals(index.getName(), "ixS4");
                    assertEquals(index.getType(), "UNIQUE_HASH_INDEX");
                    break;
                }
            }
        }
    }


}
