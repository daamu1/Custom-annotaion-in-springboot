package com.saurabh.annoation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.Set;

@Aspect
@Component
@Slf4j
public class TableChangesAspect {

    @PersistenceContext
    @Autowired
    private EntityManager entityManager;

    @Before("@annotation(trackTableChanges) && execution(* *(..))")
    @Transactional
    public void createLogTable(JoinPoint joinPoint, TrackTableChanges trackTableChanges) {
        log.info("Aspect triggered for entity: {}", joinPoint.getTarget().getClass().getName());

        Class<?> entityClass = getEntityClass(joinPoint.getTarget().getClass());

        if (entityClass != null) {
            String logTableName = entityClass.getSimpleName() + "_log";

            if (!isTableExists(logTableName)) {
                createLogTableUsingSchemaExport(entityClass);
            }
        }
    }

    private Class<?> getEntityClass(Class<?> targetClass) {
        Metamodel metamodel = entityManager.getMetamodel();
        Set<EntityType<?>> entities = metamodel.getEntities();

        for (EntityType<?> entityType : entities) {
            if (entityType.getBindableJavaType().equals(targetClass)) {
                return entityType.getJavaType();
            }
        }
        return null;
    }

    private boolean isTableExists(String tableName) {
        try {
            entityManager.createNativeQuery("SELECT 1 FROM " + tableName + " LIMIT 1")
                    .getSingleResult();
            return true;
        } catch (Exception e) {
            log.error("Error checking table existence for {}: {}", tableName, e.getMessage());
            return false;
        }
    }

    private void createLogTableUsingSchemaExport(Class<?> entityClass) {
        StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder().build();
        Metadata metadata = new MetadataSources(standardRegistry).addAnnotatedClass(entityClass).buildMetadata();

        SchemaExport schemaExport = new SchemaExport();
        schemaExport.create(EnumSet.of(TargetType.DATABASE), metadata);

        StandardServiceRegistryBuilder.destroy(standardRegistry);
        log.info("Log table created for entity: {}", entityClass.getName());
    }
}
