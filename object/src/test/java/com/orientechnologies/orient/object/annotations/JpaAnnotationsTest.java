package com.orientechnologies.orient.object.annotations;

import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Sander Postma (s.postma@magsoft.nl)
 */
public class JpaAnnotationsTest
{
    private OObjectDatabaseTx databaseTx;

    @BeforeClass
    protected void setUp() throws Exception
    {
        databaseTx = new OObjectDatabaseTx("memory:JpaAnnotationsTest");
        databaseTx.create();
        databaseTx.setAutomaticSchemaGeneration(false); // Test hybrid schema model
        databaseTx.getEntityManager().registerEntityClass(JpaTestEntity.class);
        databaseTx.getEntityManager().registerEntityClass(JpaChildEntity.class);
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
	    ; // Breakpoint location
		// TODO implement test using disk storage & database reload
    }


}
