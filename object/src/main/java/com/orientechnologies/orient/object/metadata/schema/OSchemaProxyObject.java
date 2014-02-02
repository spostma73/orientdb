/*
 *
 * Copyright 2013 Luca Molino (molino.luca--AT--gmail.com)
 * Sander Postma - 2014  Added @OIndex, @OAttributes & @Column annotation support
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.orientechnologies.orient.object.metadata.schema;

import java.lang.reflect.Field;
import java.util.*;

import com.orientechnologies.orient.core.collate.OCaseInsensitiveCollate;
import com.orientechnologies.orient.core.collate.ODefaultCollate;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.object.annotations.OAttributes;
import com.orientechnologies.orient.object.annotations.OIndex;
import javassist.util.proxy.Proxy;

import com.orientechnologies.common.exception.OException;
import com.orientechnologies.common.log.OLogManager;
import com.orientechnologies.common.reflection.OReflectionHelper;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.record.impl.ORecordBytes;
import com.orientechnologies.orient.core.storage.OStorage.CLUSTER_TYPE;
import com.orientechnologies.orient.core.type.ODocumentWrapper;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.orientechnologies.orient.object.enhancement.OObjectEntitySerializer;

import javax.persistence.Column;

/**
 * @author luca.molino
 *
 */
public class OSchemaProxyObject implements OSchema {

  protected OSchema underlying;

  public OSchemaProxyObject(OSchema iUnderlying) {
    underlying = iUnderlying;
  }

  @Override
  public int countClasses() {
    return underlying.countClasses();
  }

  @Override
  public OClass createClass(Class<?> iClass) {
    return underlying.createClass(iClass);
  }

  @Override
  public OClass createClass(Class<?> iClass, int iDefaultClusterId) {
    return underlying.createClass(iClass, iDefaultClusterId);
  }

  @Override
  public OClass createClass(String iClassName) {
    return underlying.createClass(iClassName);
  }

  @Override
  public OClass createClass(String iClassName, OClass iSuperClass) {
    return underlying.createClass(iClassName, iSuperClass);
  }

  @Override
  public OClass createClass(String iClassName, OClass iSuperClass, CLUSTER_TYPE iType) {
    return underlying.createClass(iClassName, iSuperClass, iType);
  }

  @Override
  public OClass createClass(String iClassName, int iDefaultClusterId) {
    return underlying.createClass(iClassName, iDefaultClusterId);
  }

  @Override
  public OClass createClass(String iClassName, OClass iSuperClass, int iDefaultClusterId) {
    return underlying.createClass(iClassName, iSuperClass, iDefaultClusterId);
  }

  @Override
  public OClass createClass(String iClassName, OClass iSuperClass, int[] iClusterIds) {
    return underlying.createClass(iClassName, iSuperClass, iClusterIds);
  }

  @Override
  public OClass createAbstractClass(Class<?> iClass) {
    return underlying.createAbstractClass(iClass);
  }

  @Override
  public OClass createAbstractClass(String iClassName) {
    return underlying.createAbstractClass(iClassName);
  }

  @Override
  public OClass createAbstractClass(String iClassName, OClass iSuperClass) {
    return underlying.createAbstractClass(iClassName, iSuperClass);
  }

  @Override
  public void dropClass(String iClassName) {
    underlying.dropClass(iClassName);
  }

  @Override
  public <RET extends ODocumentWrapper> RET reload() {
    return underlying.reload();
  }

  @Override
  public boolean existsClass(String iClassName) {
    return underlying.existsClass(iClassName);
  }

  @Override
  public OClass getClass(Class<?> iClass) {
    return underlying.getClass(iClass);
  }

  @Override
  public OClass getClass(String iClassName) {
    return underlying.getClass(iClassName);
  }

  @Override
  public OClass getOrCreateClass(String iClassName) {
    return underlying.getOrCreateClass(iClassName);
  }

  @Override
  public OClass getOrCreateClass(String iClassName, OClass iSuperClass) {
    return underlying.getOrCreateClass(iClassName, iSuperClass);
  }

  @Override
  public Collection<OClass> getClasses() {
    return underlying.getClasses();
  }

  @Override
  public void create() {
    underlying.create();
  }

  @Override
  @Deprecated
  public int getVersion() {
    return underlying.getVersion();
  }

  @Override
  public ORID getIdentity() {
    return underlying.getIdentity();
  }

  @Override
  public <RET extends ODocumentWrapper> RET save() {
    return underlying.save();
  }

  @Override
  public Set<OClass> getClassesRelyOnCluster(String iClusterName) {
    return underlying.getClassesRelyOnCluster(iClusterName);
  }

  public OSchema getUnderlying() {
    return underlying;
  }

  /**
   * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
   *
   * @param iPackageName
   *          The base package
   */
  public synchronized void generateSchema(final String iPackageName) {
    generateSchema(iPackageName, Thread.currentThread().getContextClassLoader());
  }

  /**
   * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
   *
   * @param iPackageName
   *          The base package
   */
  public synchronized void generateSchema(final String iPackageName, final ClassLoader iClassLoader) {
    OLogManager.instance().debug(this, "Generating schema inside package: %s", iPackageName);

    List<Class<?>> classes = null;
    try {
      classes = OReflectionHelper.getClassesFor(iPackageName, iClassLoader);
    } catch (ClassNotFoundException e) {
      throw new OException(e);
    }
    for (Class<?> c : classes) {
      generateSchema(c);
    }
  }

  /**
   * Generate/updates the SchemaClass and properties from given Class<?>.
   *
   * @param iClass
   *          :- the Class<?> to generate
   */
  public synchronized void generateSchema(final Class<?> iClass) {
    generateSchema(iClass, ODatabaseRecordThreadLocal.INSTANCE.get());
  }

  /**
   * Generate/updates the SchemaClass and properties from given Class<?>.
   *
   * @param iClass
   *          :- the Class<?> to generate
   */
  public synchronized void generateSchema(final Class<?> iClass, ODatabaseRecord database) {
    if (iClass == null || iClass.isInterface() || iClass.isPrimitive() || iClass.isEnum() || iClass.isAnonymousClass())
      return;
    OObjectEntitySerializer.registerClass(iClass);
    OClass schema = database.getMetadata().getSchema().getClass(iClass);
    if (schema == null) {
      generateOClass(iClass, database);
    }

    OIndex annIndex;
    Map<String, OIndex> indexMappings = new HashMap<String, OIndex>();
    Map<String, List<String>> indexPropNames = new HashMap<String, List<String>>();
    List<String> fields = OObjectEntitySerializer.getClassFields(iClass);
    if (fields != null)
      for (String field : fields) {
        if (schema.existsProperty(field))
          continue;
        if (OObjectEntitySerializer.isVersionField(iClass, field) || OObjectEntitySerializer.isIdField(iClass, field))
          continue;
        Field f = OObjectEntitySerializer.getField(field, iClass);
        if (f.getType().equals(Object.class) || f.getType().equals(ODocument.class) || f.getType().equals(ORecordBytes.class)) {
          continue;
        }
        OType t = OObjectEntitySerializer.getTypeByClass(iClass, field, f);
        if (t == null) {
          if (f.getType().isEnum())
            t = OType.STRING;
          else {
            t = OType.LINK;
          }
        }
        switch (t) {

        case LINK:
          Class<?> linkedClazz = f.getType();
          generateLinkProperty(database, schema, field, t, linkedClazz);
          break;
        case LINKLIST:
        case LINKMAP:
        case LINKSET:
          linkedClazz = OReflectionHelper.getGenericMultivalueType(f);
          if (linkedClazz != null)
            generateLinkProperty(database, schema, field, t, linkedClazz);
          break;

        case EMBEDDED:
          linkedClazz = f.getType();
          if (linkedClazz == null || linkedClazz.equals(Object.class) || linkedClazz.equals(ODocument.class)
              || f.getType().equals(ORecordBytes.class)) {
            continue;
          } else {
            generateLinkProperty(database, schema, field, t, linkedClazz);
          }
          break;

        case EMBEDDEDLIST:
        case EMBEDDEDSET:
        case EMBEDDEDMAP:
          linkedClazz = OReflectionHelper.getGenericMultivalueType(f);
          if (linkedClazz == null || linkedClazz.equals(Object.class) || linkedClazz.equals(ODocument.class)
              || f.getType().equals(ORecordBytes.class)) {
            continue;
          } else {
            if (OReflectionHelper.isJavaType(linkedClazz)) {
              schema.createProperty(field, t, OType.getTypeByClass(linkedClazz));
            } else if (linkedClazz.isEnum()) {
              schema.createProperty(field, t, OType.STRING);
            } else {
              generateLinkProperty(database, schema, field, t, linkedClazz);
            }
          }
          break;

        default:
          OProperty prop = schema.createProperty(field, t);
          OAttributes attributes;
          if((attributes = f.getAnnotation(OAttributes.class)) != null)
              updateAttributes(prop, attributes);
          else
          {
              Column jpaColumn;
              if((jpaColumn = f.getAnnotation(Column.class)) != null)
                updateAttributes(prop, jpaColumn);
          }
          break;
        }

        // Prepare index by ready @OIndex annotation
        if((annIndex = f.getAnnotation(OIndex.class)) != null)
        {
            String indexName = annIndex.name();
            if(indexName.length() == 0)
                indexName = iClass.getName() + '.' + f.getName();
            if(indexMappings.get(indexName) == null) // When multiple @OIndex annotations defined with the same name, types will be taken from the first one
                indexMappings.put(indexName, annIndex);
            List<String> propNames = indexPropNames.get(indexName);
            if(propNames == null)
            {
                propNames = new ArrayList<String>();
                indexPropNames.put(indexName, propNames);
            }
            if(!propNames.contains(f.getName()))
                propNames.add(f.getName());
        }
      }

      // Create indices
      for(String indexName : indexMappings.keySet())
      {
          OIndex index = indexMappings.get(indexName);
          List<String> propNames = indexPropNames.get(indexName);
          schema.createIndex(indexName, index.indexType(), propNames.toArray(new String[propNames.size()]));
          // TODO Where is key-type??
      }
  }

    private void updateAttributes(OProperty prop, OAttributes attributes)
    {
        if(attributes.min().length() > 0)
            prop.set(OProperty.ATTRIBUTES.MIN, attributes.min());
        if(attributes.max().length() > 0)
            prop.set(OProperty.ATTRIBUTES.MAX, attributes.max());
        if(attributes.regExp().length() > 0)
            prop.set(OProperty.ATTRIBUTES.REGEXP, attributes.regExp());
        prop.setCollate(attributes.collateCaseInsensitive() ? "ci" : "default");
        prop.set(OProperty.ATTRIBUTES.MANDATORY, attributes.mandatory());
        prop.set(OProperty.ATTRIBUTES.NOTNULL, attributes.notNull());
        prop.set(OProperty.ATTRIBUTES.READONLY, attributes.readOnly());
    }

    private void updateAttributes(OProperty prop, Column jpaColumn)
    {
        if(jpaColumn.length() > 0 && prop.getType() == OType.STRING) // Only string, in Orient MAX has another meaning for other types.
            prop.set(OProperty.ATTRIBUTES.MAX, jpaColumn.length());
        prop.set(OProperty.ATTRIBUTES.NOTNULL, !jpaColumn.nullable());
        prop.set(OProperty.ATTRIBUTES.READONLY, !jpaColumn.updatable());
    }


    /**
   * Checks if all registered entities has schema generated, if not it generates it
   */
  public synchronized void synchronizeSchema() {
    OObjectDatabaseTx database = ((OObjectDatabaseTx) ODatabaseRecordThreadLocal.INSTANCE.get().getDatabaseOwner());
    Collection<Class<?>> registeredEntities = database.getEntityManager().getRegisteredEntities();
    boolean automaticSchemaGeneration = database.isAutomaticSchemaGeneration();
    boolean reloadSchema = false;
    for (Class<?> iClass : registeredEntities) {
      if (Proxy.class.isAssignableFrom(iClass) || iClass.isEnum() || OReflectionHelper.isJavaType(iClass)
          || iClass.isAnonymousClass())
        return;

      if (!database.getMetadata().getSchema().existsClass(iClass.getSimpleName())) {
        database.getMetadata().getSchema().createClass(iClass.getSimpleName());
        reloadSchema = true;
      }

      for (Class<?> currentClass = iClass; currentClass != Object.class;) {

        if (automaticSchemaGeneration && !currentClass.equals(Object.class) && !currentClass.equals(ODocument.class)) {
          ((OSchemaProxyObject) database.getMetadata().getSchema()).generateSchema(currentClass, database.getUnderlying());
        }
        String iClassName = currentClass.getSimpleName();
        currentClass = currentClass.getSuperclass();

        if (currentClass == null || currentClass.equals(ODocument.class))
          // POJO EXTENDS ODOCUMENT: SPECIAL CASE: AVOID TO CONSIDER
          // ODOCUMENT FIELDS
          currentClass = Object.class;

        if (database != null && !database.isClosed() && !currentClass.equals(Object.class)) {
          OClass oSuperClass;
          OClass currentOClass = database.getMetadata().getSchema().getClass(iClassName);
          if (!database.getMetadata().getSchema().existsClass(currentClass.getSimpleName())) {
            oSuperClass = database.getMetadata().getSchema().createClass(currentClass.getSimpleName());
            reloadSchema = true;
          } else {
            oSuperClass = database.getMetadata().getSchema().getClass(currentClass.getSimpleName());
            reloadSchema = true;
          }

          if (currentOClass.getSuperClass() == null || !currentOClass.getSuperClass().equals(oSuperClass)) {
            currentOClass.setSuperClass(oSuperClass);
            reloadSchema = true;
          }

        }
      }
    }
    if (database != null && !database.isClosed() && reloadSchema) {
      database.getMetadata().getSchema().save();
      database.getMetadata().getSchema().reload();
    }
  }

  protected static void generateOClass(Class<?> iClass, ODatabaseRecord database) {
    boolean reloadSchema = false;
    for (Class<?> currentClass = iClass; currentClass != Object.class;) {
      String iClassName = currentClass.getSimpleName();
      currentClass = currentClass.getSuperclass();

      if (currentClass == null || currentClass.equals(ODocument.class))
        // POJO EXTENDS ODOCUMENT: SPECIAL CASE: AVOID TO CONSIDER
        // ODOCUMENT FIELDS
        currentClass = Object.class;

      if (ODatabaseRecordThreadLocal.INSTANCE.get() != null && !ODatabaseRecordThreadLocal.INSTANCE.get().isClosed()
          && !currentClass.equals(Object.class)) {
        OClass oSuperClass;
        OClass currentOClass = database.getMetadata().getSchema().getClass(iClassName);
        if (!database.getMetadata().getSchema().existsClass(currentClass.getSimpleName())) {
          oSuperClass = database.getMetadata().getSchema().createClass(currentClass.getSimpleName());
          reloadSchema = true;
        } else {
          oSuperClass = database.getMetadata().getSchema().getClass(currentClass.getSimpleName());
          reloadSchema = true;
        }

        if (currentOClass.getSuperClass() == null || !currentOClass.getSuperClass().equals(oSuperClass)) {
          currentOClass.setSuperClass(oSuperClass);
          reloadSchema = true;
        }

      }
    }
    if (reloadSchema) {
      database.getMetadata().getSchema().save();
      database.getMetadata().getSchema().reload();
    }
  }

  protected static void generateLinkProperty(ODatabaseRecord database, OClass schema, String field, OType t, Class<?> linkedClazz) {
    OClass linkedClass = database.getMetadata().getSchema().getClass(linkedClazz);
    if (linkedClass == null) {
      OObjectEntitySerializer.registerClass(linkedClazz);
      linkedClass = database.getMetadata().getSchema().getClass(linkedClazz);
    }
    schema.createProperty(field, t, linkedClass);
  }

}
