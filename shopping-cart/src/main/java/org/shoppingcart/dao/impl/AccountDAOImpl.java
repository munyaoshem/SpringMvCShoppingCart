package org.shoppingcart.dao.impl;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.shoppingcart.dao.AccountDAO;
import org.shoppingcart.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;

//Transactional for Hibernate
@Transactional
public class AccountDAOImpl implements AccountDAO {

	private static final Logger LOGEVENT = Logger.getLogger(AccountDAOImpl.class);

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public Account findAccount(String username) {
		Session session = sessionFactory.getCurrentSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Account> criteriaQuery = criteriaBuilder.createQuery(Account.class);
		Root<Account> root = criteriaQuery.from(Account.class);
		criteriaQuery.where(criteriaBuilder.equal(root.get("username"), username));
		criteriaQuery.select(root);
		Query<Account> query = session.createQuery(criteriaQuery);

		if (query.getResultList().isEmpty()) {
			LOGEVENT.error("AccountDAOImpl -> findAccount -> resultList: " + query.getResultList().size());
		}
		return query.getSingleResult();
	}
}