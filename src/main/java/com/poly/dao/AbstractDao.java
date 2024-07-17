package com.poly.dao;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.TypedQuery;

import com.poly.util.JpaUtil;

public class AbstractDao<T> {
	public static final EntityManager entityManager = JpaUtil.getEntityManager();
	@SuppressWarnings("deprecation")
	@Override
	protected void finalize() throws Throwable {
		entityManager.close();
		super.finalize();
	}
	
	public T findById(Class<T> clazz, Integer id) {
		return entityManager.find(clazz, id);
	}
	
	public List<T> findAll(Class<T> clazz, boolean existIsActive) {
		String entityName = clazz.getSimpleName();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT o FROM ").append(entityName).append(" o");
		if(existIsActive == true) {
			sql.append(" WHERE isActive = 1");
		}
		TypedQuery<T> query = entityManager.createQuery(sql.toString(), clazz);
		return query.getResultList();
	}
	
	public List<T> findAll(Class<T> clazz, boolean existIsActive, int pageNumber, int pageSize) {
		String entityName = clazz.getSimpleName();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT o FROM ").append(entityName).append(" o");
		if(existIsActive == true) {
			sql.append(" WHERE isActive = 1");
		}
		TypedQuery<T> query = entityManager.createQuery(sql.toString(), clazz);
		query.setFirstResult((pageNumber - 1) * pageSize);
		query.setMaxResults(pageSize);
		return query.getResultList();
	}
	//select o from o user o where 0.username = ?0 and o.password  = ?1;
	
	public T  findOne(Class<T> clazz, String sql, Object... params) {
		TypedQuery<T> query = entityManager.createQuery(sql, clazz);
		for(int i = 0; i< params.length; i++) {
			query.setParameter(i, params[i]);
		}
		List<T> reslut = query.getResultList();
		if (reslut.isEmpty()) {
			return null;
		}
		return reslut.get(0);
	}
	
	public List<T>  findMany(Class<T> clazz, String sql, Object... params) {
		TypedQuery<T> query = entityManager.createQuery(sql, clazz);
		for(int i = 0; i< params.length; i++) {
			query.setParameter(i, params[i]);
		}
		return query.getResultList();
		}
		
	
	@SuppressWarnings("unchecked")
	public List<Object[]>  findManyByNativeQuery( String sql, Object... params) {
		Query query = entityManager.createNativeQuery(sql);
		for(int i = 0; i< params.length; i++) {
			query.setParameter(i, params[i]);
		}
		return  query.getResultList();
		}
	
	public T create(T entity) {
		try {
			entityManager.getTransaction().begin();
			entityManager.persist(entity);
			entityManager.getTransaction().commit();
			System.out.println("Create Succeed");
			return entity;
		} catch (Exception e) {
			entityManager.getTransaction().rollback();
			System.out.println("Cannot insert entity " + entity.getClass().getSimpleName() + " to DB");
			throw new RuntimeException(e);
		}
	}
	
	public T update(T entity) {
		try {
			entityManager.getTransaction().begin();
			entityManager.merge(entity);
			entityManager.getTransaction().commit();
			System.out.println("Update Succeed");
			return entity;
		} catch (Exception e) {
			entityManager.getTransaction().rollback();
			System.out.println("Cannot update entity " + entity.getClass().getSimpleName() + " to DB");
			throw new RuntimeException(e);
		}
	}
	
	public T delete(T entity) {
		try {
			entityManager.getTransaction().begin();
			entityManager.remove(entity);
			entityManager.getTransaction().commit();
			System.out.println("Delete Succeed");
			return entity;
		} catch (Exception e) {
			entityManager.getTransaction().rollback();
			System.out.println("Cannot delete entity " + entity.getClass().getSimpleName() + " to DB");
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<T> callStored(String namedStored, Map<String, Object> params) {
		StoredProcedureQuery query = entityManager.createNamedStoredProcedureQuery(namedStored);
		params.forEach((key, value) -> query.setParameter(key, value));
		return (List<T>) query.getResultList();
	}
}
